package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.PlanPersonnalise;
import com.autiguide.autiguide.repository.PlanPersonnaliseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanPersonnaliseService {

    private final PlanPersonnaliseRepository planRepository;

    public PlanPersonnaliseService(PlanPersonnaliseRepository planRepository) {
        this.planRepository = planRepository;
    }

    // 🔹 récupérer tous les plans
    public List<PlanPersonnalise> getAllPlans() {
        return planRepository.findAll();
    }

    // 🔹 récupérer plan par id résultat
    public PlanPersonnalise getByResultatId(Long resultatId) {
        return planRepository.findByResultatId(resultatId)
                .orElseThrow(() -> new RuntimeException("Plan introuvable"));
    }

    // 🔹 sauvegarder plan (si besoin)
    public PlanPersonnalise save(PlanPersonnalise plan) {
        return planRepository.save(plan);
    }
}