package com.telecom.controller;

import com.telecom.model.dto.ApiResponse;
import com.telecom.model.dto.AuthDto;
import com.telecom.model.entity.User;
import com.telecom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="https://telecom-plan-comparator-frontend.onrender.com")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, user management and admin APIs")
public class AuthController {

    private final AuthService authService;

    // ─── Public: Register as USER ──────────────────────────────────

    @PostMapping("/register")
    @Operation(summary = "Register a new user (ROLE_USER)")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {

        AuthDto.UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", user));
    }

    // ─── Admin: Register as ADMIN ──────────────────────────────────

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new admin user [ADMIN only]")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> registerAdmin(
            @Valid @RequestBody AuthDto.RegisterRequest request) {

        AuthDto.UserResponse user = authService.registerWithRole(request, User.Role.ROLE_ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", user));
    }

    // ─── Public: Login ─────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<ApiResponse<AuthDto.JwtResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {

        AuthDto.JwtResponse jwt = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwt));
    }

    // ─── Authenticated: Get own profile ────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        AuthDto.UserResponse user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // ─── Authenticated: Change own password ────────────────────────

    @PutMapping("/me/password")
    @Operation(summary = "Change current user's password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody AuthDto.ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    // ─── Admin: List all users ─────────────────────────────────────

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all registered users [ADMIN only]")
    public ResponseEntity<ApiResponse<List<AuthDto.UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(authService.getAllUsers()));
    }

    // ─── Admin: Get user by ID ─────────────────────────────────────

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID [ADMIN only]")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authService.getUserById(id)));
    }

    // ─── Admin: Update user roles ──────────────────────────────────

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update roles for a user [ADMIN only]")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AuthDto.UpdateRoleRequest request) {

        AuthDto.UserResponse updated = authService.updateUserRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("User roles updated", updated));
    }

    // ─── Admin: Enable / Disable user ─────────────────────────────

    @PutMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a user account [ADMIN only]")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User enabled", authService.setUserEnabled(id, true)));
    }

    @PutMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a user account [ADMIN only]")
    public ResponseEntity<ApiResponse<AuthDto.UserResponse>> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User disabled", authService.setUserEnabled(id, false)));
    }

    // ─── Root Admin: Reset any user's password ─────────────────────

    @PutMapping("/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset a user's password [ADMIN only — root admin use]")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @PathVariable Long id,
            @Valid @RequestBody AuthDto.AdminResetPasswordRequest request) {

        authService.resetUserPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }
}
