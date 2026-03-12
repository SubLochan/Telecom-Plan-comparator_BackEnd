package com.telecom.service;

import org.springframework.stereotype.Service;

import com.telecom.entity.Plan;
import com.telecom.repository.PlanRepository;

import java.util.List;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<Plan> getPlansByOperator(String operatorName) {
        return planRepository.findByOperatorName(operatorName);
    }

    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }
}
