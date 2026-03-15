package com.telecom.repository;

import com.telecom.model.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    Page<Plan> findByStatus(Plan.PlanStatus status, Pageable pageable);

    Page<Plan> findByProviderIgnoreCaseAndStatus(String provider, Plan.PlanStatus status, Pageable pageable);

    Page<Plan> findByPlanTypeAndStatus(Plan.PlanType planType, Plan.PlanStatus status, Pageable pageable);

    Page<Plan> findByMonthlyPriceLessThanEqualAndStatus(BigDecimal maxPrice, Plan.PlanStatus status, Pageable pageable);

    @Query("SELECT DISTINCT p.provider FROM Plan p WHERE p.status = 'ACTIVE' ORDER BY p.provider")
    List<String> findAllActiveProviders();

    @Query("SELECT p FROM Plan p WHERE p.status = 'ACTIVE' " +
           "AND (:provider IS NULL OR LOWER(p.provider) = LOWER(:provider)) " +
           "AND (:planType IS NULL OR p.planType = :planType) " +
           "AND (:maxPrice IS NULL OR p.monthlyPrice <= :maxPrice) " +
           "AND (:minPrice IS NULL OR p.monthlyPrice >= :minPrice) " +
           "AND (:fiveG IS NULL OR p.fiveGEnabled = :fiveG) " +
           "AND (:roaming IS NULL OR p.internationalRoaming = :roaming) " +
           "AND (:hotspot IS NULL OR p.hotspotEnabled = :hotspot)")
    Page<Plan> findWithFilters(
            @Param("provider") String provider,
            @Param("planType") Plan.PlanType planType,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minPrice") BigDecimal minPrice,
            @Param("fiveG") Boolean fiveG,
            @Param("roaming") Boolean roaming,
            @Param("hotspot") Boolean hotspot,
            Pageable pageable);

    @Query("SELECT p FROM Plan p WHERE p.id IN :ids AND p.status = 'ACTIVE'")
    List<Plan> findAllByIdInAndStatusActive(@Param("ids") List<Long> ids);

    boolean existsByNameIgnoreCaseAndProviderIgnoreCase(String name, String provider);
}
