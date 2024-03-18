package com.maurice.DocumentManagement.controllers;


import com.maurice.DocumentManagement.dto.PlanDto;
import com.maurice.DocumentManagement.services.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public List<PlanDto> getAllPlans() {
        return planService.getAllPlans();
    }

    @PostMapping
    public ResponseEntity<?> registerPlan(@RequestBody PlanDto plan) {
        planService.registerPlan(plan);
        return ResponseEntity.status(201).body(null);
    }


}
