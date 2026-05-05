package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.PlanPersonnalise;
import com.autiguide.autiguide.repository.PlanPersonnaliseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanPersonnaliseRepository planRepo;

    public PlanController(PlanPersonnaliseRepository planRepo) {
        this.planRepo = planRepo;
    }

    @GetMapping
    public List<PlanPersonnalise> getAll() {
        return planRepo.findAll();
    }
}