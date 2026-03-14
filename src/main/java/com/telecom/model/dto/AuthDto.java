package com.telecom.model.dto;

import com.telecom.model.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

// ─── Auth DTOs ────────────────────────────────────────────────

public class AuthDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 6, max = 100)
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private Set<User.Role> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private Set<User.Role> roles;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRoleRequest {
        @NotNull
        private Set<User.Role> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AdminResetPasswordRequest {
        @NotBlank @Size(min = 6, max = 100)
        private String newPassword;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;
        @NotBlank @Size(min = 6, max = 100)
        private String newPassword;
    }
}
