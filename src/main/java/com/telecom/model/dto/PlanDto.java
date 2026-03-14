package com.telecom.model.dto;

import com.telecom.model.entity.Plan;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ─── Plan DTOs ────────────────────────────────────────────────

public class PlanDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String provider;
        private Plan.PlanType planType;
        private BigDecimal monthlyPrice;
        private Integer dataLimitGB;
        private Integer callMinutes;
        private Integer smsCount;
        private Boolean fiveGEnabled;
        private Boolean internationalRoaming;
        private Boolean hotspotEnabled;
        private String description;
        private List<String> additionalFeatures;
        private Plan.PlanStatus status;
        private Integer contractMonths;
        private BigDecimal setupFee;
        private Double averageRating;
        private Long reviewCount;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank private String name;
        @NotBlank private String provider;
        @NotNull  private Plan.PlanType planType;
        @NotNull @DecimalMin("0.0") private BigDecimal monthlyPrice;
        private Integer dataLimitGB;
        private Integer callMinutes;
        private Integer smsCount;
        private Boolean fiveGEnabled = false;
        private Boolean internationalRoaming = false;
        private Boolean hotspotEnabled = false;
        @Size(max = 1000) private String description;
        private List<String> additionalFeatures;
        private Integer contractMonths = 0;
        private BigDecimal setupFee = BigDecimal.ZERO;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String name;
        private String provider;
        private Plan.PlanType planType;
        @DecimalMin("0.0") private BigDecimal monthlyPrice;
        private Integer dataLimitGB;
        private Integer callMinutes;
        private Integer smsCount;
        private Boolean fiveGEnabled;
        private Boolean internationalRoaming;
        private Boolean hotspotEnabled;
        @Size(max = 1000) private String description;
        private List<String> additionalFeatures;
        private Plan.PlanStatus status;
        private Integer contractMonths;
        private BigDecimal setupFee;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterRequest {
        private String provider;
        private Plan.PlanType planType;
        private BigDecimal maxMonthlyPrice;
        private BigDecimal minMonthlyPrice;
        private Integer minDataLimitGB;
        private Boolean fiveGEnabled;
        private Boolean internationalRoaming;
        private Boolean hotspotEnabled;
        private Integer maxContractMonths;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComparisonResponse {
        private List<Response> plans;
        private ComparisonSummary summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComparisonSummary {
        private Response cheapest;
        private Response mostData;
        private Response bestRated;
        private Response bestValue;
    }
}
