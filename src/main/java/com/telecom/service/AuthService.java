package com.telecom.service;

import com.telecom.config.JwtUtils;
import com.telecom.model.dto.AuthDto;
import com.telecom.model.entity.User;
import com.telecom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // ─── Register (USER role) ──────────────────────────────────────

    public AuthDto.UserResponse register(AuthDto.RegisterRequest request) {
        return registerWithRole(request, User.Role.ROLE_USER);
    }

    // ─── Register with explicit role (called by admin endpoint) ───

    public AuthDto.UserResponse registerWithRole(AuthDto.RegisterRequest request, User.Role role) {
        validateUniqueCredentials(request.getUsername(), request.getEmail());

        // Admin registrations always include ROLE_USER as well
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        roles.add(role);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new {} user: {}", role, saved.getUsername());
        return toUserResponse(saved);
    }

    // ─── Login ─────────────────────────────────────────────────────

    public AuthDto.JwtResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUsername()));

        log.info("User logged in: {}", user.getUsername());

        return AuthDto.JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    // ─── Get Current User ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthDto.UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return toUserResponse(user);
    }

    // ─── Get All Users (Admin) ─────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AuthDto.UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // ─── Get User By ID (Admin) ────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthDto.UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toUserResponse(user);
    }

    // ─── Update User Role (Admin) ──────────────────────────────────

    public AuthDto.UserResponse updateUserRole(Long userId, AuthDto.UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Set<User.Role> updatedRoles = new HashSet<>(request.getRoles());
        // Always retain ROLE_USER — every account keeps base access
        updatedRoles.add(User.Role.ROLE_USER);
        user.setRoles(updatedRoles);

        User saved = userRepository.save(user);
        log.info("Updated roles for user {}: {}", saved.getUsername(), saved.getRoles());
        return toUserResponse(saved);
    }

    // ─── Change Password ───────────────────────────────────────────

    public void changePassword(String username, AuthDto.ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    // ─── Disable / Enable User (Admin) ────────────────────────────

    public AuthDto.UserResponse setUserEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        log.info("User {} enabled={}", saved.getUsername(), enabled);
        return toUserResponse(saved);
    }

    // ─── Reset User Password (Root Admin only) ───────────────────────

    public void resetUserPassword(Long userId, AuthDto.AdminResetPasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset by admin for user: {}", user.getUsername());
    }

    // ─── Helpers ───────────────────────────────────────────────────

    private void validateUniqueCredentials(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use: " + email);
        }
    }

    private AuthDto.UserResponse toUserResponse(User user) {
        return AuthDto.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
