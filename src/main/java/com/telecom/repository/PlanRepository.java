package com.telecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByOperatorName(String operatorName);
}
