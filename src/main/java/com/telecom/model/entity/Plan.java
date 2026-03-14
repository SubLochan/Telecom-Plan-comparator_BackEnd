package com.telecom.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column
    private Integer dataLimitGB;       // null = unlimited

    @Column
    private Integer callMinutes;       // null = unlimited

    @Column
    private Integer smsCount;          // null = unlimited

    @Column(nullable = false)
    private Boolean fiveGEnabled = false;

    @Column(nullable = false)
    private Boolean internationalRoaming = false;

    @Column(nullable = false)
    private Boolean hotspotEnabled = false;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> additionalFeatures;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status = PlanStatus.ACTIVE;

    @Column(nullable = false)
    private Integer contractMonths = 0;   // 0 = no contract

    @Column(precision = 10, scale = 2)
    private BigDecimal setupFee = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum PlanType {
        PREPAID, POSTPAID, FAMILY, BUSINESS, STUDENT
    }

    public enum PlanStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
}
