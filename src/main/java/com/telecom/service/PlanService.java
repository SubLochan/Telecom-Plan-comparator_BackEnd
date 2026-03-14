package com.telecom.service;

import com.telecom.model.dto.PlanDto;
import com.telecom.model.entity.Plan;
import com.telecom.repository.PlanRepository;
import com.telecom.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final ReviewRepository reviewRepository;

    // ─── CRUD ─────────────────────────────────────────────────────

    @Transactional
    public PlanDto.Response createPlan(PlanDto.CreateRequest request) {
        if (planRepository.existsByNameIgnoreCaseAndProviderIgnoreCase(request.getName(), request.getProvider())) {
            throw new IllegalArgumentException("Plan '" + request.getName() + "' already exists for provider " + request.getProvider());
        }

        Plan plan = Plan.builder()
                .name(request.getName())
                .provider(request.getProvider())
                .planType(request.getPlanType())
                .monthlyPrice(request.getMonthlyPrice())
                .dataLimitGB(request.getDataLimitGB())
                .callMinutes(request.getCallMinutes())
                .smsCount(request.getSmsCount())
                .fiveGEnabled(Boolean.TRUE.equals(request.getFiveGEnabled()))
                .internationalRoaming(Boolean.TRUE.equals(request.getInternationalRoaming()))
                .hotspotEnabled(Boolean.TRUE.equals(request.getHotspotEnabled()))
                .description(request.getDescription())
                .additionalFeatures(request.getAdditionalFeatures())
                .contractMonths(request.getContractMonths() != null ? request.getContractMonths() : 0)
                .setupFee(request.getSetupFee() != null ? request.getSetupFee() : BigDecimal.ZERO)
                .status(Plan.PlanStatus.ACTIVE)
                .build();

        Plan saved = planRepository.save(plan);
        log.info("Created plan id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    public PlanDto.Response getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + id));
        return toResponse(plan);
    }

    public Page<PlanDto.Response> getAllPlans(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return planRepository.findByStatus(Plan.PlanStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    public Page<PlanDto.Response> filterPlans(PlanDto.FilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("monthlyPrice").ascending());
        return planRepository.findWithFilters(
                filter.getProvider(),
                filter.getPlanType(),
                filter.getMaxMonthlyPrice(),
                filter.getMinMonthlyPrice(),
                filter.getFiveGEnabled(),
                filter.getInternationalRoaming(),
                filter.getHotspotEnabled(),
                pageable
        ).map(this::toResponse);
    }

    @Transactional
    public PlanDto.Response updatePlan(Long id, PlanDto.UpdateRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + id));

        if (request.getName() != null)               plan.setName(request.getName());
        if (request.getProvider() != null)           plan.setProvider(request.getProvider());
        if (request.getPlanType() != null)           plan.setPlanType(request.getPlanType());
        if (request.getMonthlyPrice() != null)       plan.setMonthlyPrice(request.getMonthlyPrice());
        if (request.getDataLimitGB() != null)        plan.setDataLimitGB(request.getDataLimitGB());
        if (request.getCallMinutes() != null)        plan.setCallMinutes(request.getCallMinutes());
        if (request.getSmsCount() != null)           plan.setSmsCount(request.getSmsCount());
        if (request.getFiveGEnabled() != null)       plan.setFiveGEnabled(request.getFiveGEnabled());
        if (request.getInternationalRoaming() != null) plan.setInternationalRoaming(request.getInternationalRoaming());
        if (request.getHotspotEnabled() != null)    plan.setHotspotEnabled(request.getHotspotEnabled());
        if (request.getDescription() != null)        plan.setDescription(request.getDescription());
        if (request.getAdditionalFeatures() != null) plan.setAdditionalFeatures(request.getAdditionalFeatures());
        if (request.getStatus() != null)             plan.setStatus(request.getStatus());
        if (request.getContractMonths() != null)     plan.setContractMonths(request.getContractMonths());
        if (request.getSetupFee() != null)           plan.setSetupFee(request.getSetupFee());

        return toResponse(planRepository.save(plan));
    }

    @Transactional
    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + id));
        plan.setStatus(Plan.PlanStatus.DISCONTINUED);
        planRepository.save(plan);
        log.info("Soft-deleted plan id={}", id);
    }

    // ─── Comparison ────────────────────────────────────────────────

    public PlanDto.ComparisonResponse comparePlans(List<Long> ids) {
        if (ids == null || ids.size() < 2) {
            throw new IllegalArgumentException("At least 2 plan IDs are required for comparison");
        }
        if (ids.size() > 5) {
            throw new IllegalArgumentException("Maximum 5 plans can be compared at once");
        }

        List<Plan> plans = planRepository.findAllByIdInAndStatusActive(ids);
        if (plans.size() != ids.size()) {
            throw new EntityNotFoundException("One or more plans not found or inactive");
        }

        List<PlanDto.Response> responses = plans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PlanDto.ComparisonSummary summary = buildSummary(responses);

        return PlanDto.ComparisonResponse.builder()
                .plans(responses)
                .summary(summary)
                .build();
    }

    private PlanDto.ComparisonSummary buildSummary(List<PlanDto.Response> plans) {
        PlanDto.Response cheapest = plans.stream()
                .min(Comparator.comparing(PlanDto.Response::getMonthlyPrice))
                .orElse(null);

        PlanDto.Response mostData = plans.stream()
                .filter(p -> p.getDataLimitGB() != null)
                .max(Comparator.comparing(PlanDto.Response::getDataLimitGB))
                .orElse(plans.get(0)); // all unlimited

        PlanDto.Response bestRated = plans.stream()
                .filter(p -> p.getAverageRating() != null)
                .max(Comparator.comparing(PlanDto.Response::getAverageRating))
                .orElse(null);

        // Best value = highest data per dollar
        PlanDto.Response bestValue = plans.stream()
                .filter(p -> p.getDataLimitGB() != null && p.getMonthlyPrice().compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparingDouble(p ->
                        p.getDataLimitGB() / p.getMonthlyPrice().doubleValue()))
                .orElse(cheapest);

        return PlanDto.ComparisonSummary.builder()
                .cheapest(cheapest)
                .mostData(mostData)
                .bestRated(bestRated)
                .bestValue(bestValue)
                .build();
    }

    // ─── Misc ──────────────────────────────────────────────────────

    public List<String> getAllProviders() {
        return planRepository.findAllActiveProviders();
    }

    // ─── Mapper ────────────────────────────────────────────────────

    PlanDto.Response toResponse(Plan plan) {
        Double avgRating = reviewRepository.findAverageRatingByPlanId(plan.getId());
        Long reviewCount = reviewRepository.countByPlanId(plan.getId());

        return PlanDto.Response.builder()
                .id(plan.getId())
                .name(plan.getName())
                .provider(plan.getProvider())
                .planType(plan.getPlanType())
                .monthlyPrice(plan.getMonthlyPrice())
                .dataLimitGB(plan.getDataLimitGB())
                .callMinutes(plan.getCallMinutes())
                .smsCount(plan.getSmsCount())
                .fiveGEnabled(plan.getFiveGEnabled())
                .internationalRoaming(plan.getInternationalRoaming())
                .hotspotEnabled(plan.getHotspotEnabled())
                .description(plan.getDescription())
                .additionalFeatures(plan.getAdditionalFeatures())
                .status(plan.getStatus())
                .contractMonths(plan.getContractMonths())
                .setupFee(plan.getSetupFee())
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
