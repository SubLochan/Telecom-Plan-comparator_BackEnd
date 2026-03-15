package com.telecom.repository;

import com.telecom.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByPlanId(Long planId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Optional<Review> findByPlanIdAndUserId(Long planId, Long userId);

    boolean existsByPlanIdAndUserId(Long planId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.plan.id = :planId")
    Double findAverageRatingByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.plan.id = :planId")
    Long countByPlanId(@Param("planId") Long planId);
}
