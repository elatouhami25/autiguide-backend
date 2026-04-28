package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.*;
import com.autiguide.autiguide.service.ClaudeAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resultat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ResultatAIController {

    private final ClaudeAIService claudeAIService;
    private final ResultatRepository resultatRepository;
    private final PlanPersonnaliseRepository planRepository;
    private final EnfantRepository enfantRepository;
    private final QuestionnaireRepository questionnaireRepository;

    /**
     * Endpoint 1 : Calculer le score + Générer le plan IA.
     *
     * Le parent envoie les réponses → le système calcule le score
     * → l'IA génère le plan personnalisé automatiquement.
     *
     * URL : POST /api/resultat/calculer
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "questionnaireId": 1,
     *   "questions": ["Question 1", "Question 2"],
     *   "reponses": [true, false, true, false, true]
     * }
     */
    @PostMapping("/calculer")
    public ResponseEntity<AIResponse> calculerEtGenererPlan(
            @RequestBody Map<String, Object> body) {

        // Extraction des données du body
        Long enfantId = Long.valueOf(body.get("enfantId").toString());
        List<String> questions = (List<String>) body.get("questions");
        List<Boolean> reponses = (List<Boolean>) body.get("reponses");

        // Récupération de l'enfant
        Enfant enfant = enfantRepository.findById(enfantId)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable"));

        // Calcul du score : nombre de réponses "Oui" (true)
        int score = (int) reponses.stream().filter(r -> r).count();

        // Classification du niveau de risque selon le score
        NiveauRisque niveau;
        if (score <= 3)      niveau = NiveauRisque.FAIBLE;
        else if (score <= 7) niveau = NiveauRisque.MOYEN;
        else                 niveau = NiveauRisque.ELEVE;

        // Sauvegarde du résultat en DB
        Resultat resultat = new Resultat();
        resultat.setEnfant(enfant);
        resultat.setScore(score);
        resultat.setNiveauRisque(niveau);
        resultat.setDateEvaluation(LocalDate.now());
        resultatRepository.save(resultat);

        // Génération du plan personnalisé via Claude IA
        String planContenu = claudeAIService.genererPlanPersonnalise(
                resultat, questions, reponses
        );

        // Sauvegarde du plan en DB
        PlanPersonnalise plan = new PlanPersonnalise();
        plan.setResultat(resultat);
        plan.setTitre("Plan - Niveau " + niveau + " - Score " + score);
        plan.setDescription(planContenu);
        plan.setObjectifs("Générés par IA Claude");
        plan.setDateGeneration(LocalDate.now());
        planRepository.save(plan);

        log.info("Résultat sauvegardé - score: {} niveau: {}", score, niveau);

        return ResponseEntity.ok(new AIResponse(
                niveau.toString(),
                String.valueOf(score),
                planContenu,
                "✅ Score calculé et plan généré avec succès"
        ));
    }

    /**
     * Endpoint 2 : Consulter le résultat + plan d'un enfant.
     * URL : GET /api/resultat/enfant/{enfantId}
     */
    @GetMapping("/enfant/{enfantId}")
    public ResponseEntity<?> consulterResultat(
            @PathVariable Long enfantId) {

        List<Resultat> resultats = resultatRepository
                .findByEnfantIdOrderByDateEvaluationDesc(enfantId);

        if (resultats.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "Aucun résultat trouvé pour cet enfant")
            );
        }

        return ResponseEntity.ok(resultats);
    }
}