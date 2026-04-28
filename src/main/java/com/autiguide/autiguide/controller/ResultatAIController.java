package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.PlanPersonnaliseRepository;
import com.autiguide.autiguide.service.ResultatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controller pour le calcul des résultats TSA
 * et la génération des plans personnalisés via IA.
 */
@RestController
@RequestMapping("/api/resultat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResultatAIController {

    private final ResultatService resultatService;
    private final PlanPersonnaliseRepository planRepository;

    /**
     * POST /api/resultat/calculer
     * Calcule le score + génère le plan IA + sauvegarde tout en DB.
     *
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "questions": ["Question 1", ...],
     *   "reponses": [true, false, ...]
     * }
     */
    @PostMapping("/calculer")
    public ResponseEntity<AIResponse> calculerEtGenererPlan(
            @RequestBody Map<String, Object> body) {

        Long enfantId = Long.valueOf(body.get("enfantId").toString());
        List<String> questions = (List<String>) body.get("questions");
        List<Boolean> reponses = (List<Boolean>) body.get("reponses");

        // Délégation au service
        Resultat resultat = resultatService.calculerEtSauvegarder(
                enfantId, questions, reponses
        );

        // Récupérer le plan sauvegardé
        PlanPersonnalise plan = planRepository
                .findByResultatId(resultat.getId())
                .orElseThrow();

        return ResponseEntity.ok(new AIResponse(
                resultat.getNiveauRisque().toString(),
                String.valueOf(resultat.getScore()),
                plan.getDescription(),
                "✅ Score calculé, résultat et plan sauvegardés en DB"
        ));
    }

    /**
     * GET /api/resultat/enfant/{enfantId}
     * Consulter tous les résultats d'un enfant.
     */
    @GetMapping("/enfant/{enfantId}")
    public ResponseEntity<?> consulterResultats(
            @PathVariable Long enfantId) {

        List<Resultat> resultats = resultatService.consulterResultats(enfantId);

        if (resultats.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "Aucun résultat trouvé pour cet enfant")
            );
        }
        return ResponseEntity.ok(resultats);
    }

    /**
     * GET /api/resultat/{resultatId}/plan
     * Consulter le plan personnalisé d'un résultat.
     */
    @GetMapping("/{resultatId}/plan")
    public ResponseEntity<?> consulterPlan(
            @PathVariable Long resultatId) {

        PlanPersonnalise plan = resultatService.consulterPlan(resultatId);
        return ResponseEntity.ok(plan);
    }
}