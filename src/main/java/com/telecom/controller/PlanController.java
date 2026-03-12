package com.telecom.controller;

import org.springframework.web.bind.annotation.*;

import com.telecom.entity.Plan;
import com.telecom.service.PlanService;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }

    @GetMapping("/{operator}")
    public List<Plan> getPlansByOperator(@PathVariable String operator) {
        return planService.getPlansByOperator(operator);
    }

    @PostMapping
    public Plan addPlan(@RequestBody Plan plan) {
        return planService.savePlan(plan);
    }
}