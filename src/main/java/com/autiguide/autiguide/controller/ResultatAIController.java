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
import com.autiguide.autiguide.exception.ResourceNotFoundException;

/**
 * Controller pour le calcul des résultats TSA.
 *
 * Le score est calculé en Java pur (ResultatService).
 * L'IA génère uniquement le plan personnalisé.
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
     * Calcule le score (Java pur) + génère le plan IA + sauvegarde en DB.
     *
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "questions": ["Question 1", ...],
     *   "reponses": [true, false, ...],
     *   "age": 3   ← optionnel, calculé depuis la DB si absent
     * }
     */
    @PostMapping("/calculer")
    public ResponseEntity<AIResponse> calculerEtGenererPlan(
            @RequestBody Map<String, Object> body) {

        Long enfantId = Long.valueOf(body.get("enfantId").toString());
        List<String> questions = (List<String>) body.get("questions");
        List<Boolean> reponses = (List<Boolean>) body.get("reponses");

        // Récupérer l'âge si fourni par le frontend
        Integer age = null;
        if (body.get("age") != null) {
            age = Integer.valueOf(body.get("age").toString());
        }

        // Délégation au service (calcul Java pur + plan IA)
        Resultat resultat = resultatService.calculerEtSauvegarder(
                enfantId, questions, reponses, age
        );

        // Récupérer le plan sauvegardé
        PlanPersonnalise plan = planRepository
                .findByResultatId(resultat.getId())
                .orElseThrow();

        // Déterminer la tranche d'âge pour l'affichage
        int ageEnfant = (age != null) ? age : resultat.getEnfant().calculerAge();
        String tranche = ResultatService.determinerTranche(ageEnfant);

        return ResponseEntity.ok(new AIResponse(
                resultat.getNiveauRisque().toString(),
                String.valueOf(resultat.getScore()),
                plan.getDescription(),
                "✅ Score calculé (Java), plan généré (IA) — Tranche : " + tranche
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
