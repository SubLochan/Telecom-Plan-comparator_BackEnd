package com.telecom.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long planId;
        private String planName;
        private Long userId;
        private String username;
        @Min(1) @Max(5)
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotNull @Min(1) @Max(5)
        private Integer rating;
        @Size(max = 1000)
        private String comment;
    }
}
