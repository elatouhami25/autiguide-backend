package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsable du calcul du score TSA,
 * de la classification du niveau de risque,
 * et de la génération du plan personnalisé via IA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final PlanPersonnaliseRepository planRepository;
    private final EnfantRepository enfantRepository;
    private final ClaudeAIService claudeAIService;

    /**
     * Calcule le score, classifie le risque,
     * sauvegarde le résultat ET génère le plan IA.
     *
     * @param enfantId        ID de l'enfant
     * @param questions       liste des questions posées
     * @param reponses        liste des réponses (true=Oui, false=Non)
     * @return Resultat sauvegardé avec plan généré
     */
    public Resultat calculerEtSauvegarder(
            Long enfantId,
            List<String> questions,
            List<Boolean> reponses) {

        // 1. Récupérer l'enfant
        Enfant enfant = enfantRepository.findById(enfantId)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable id: " + enfantId));

        // 2. Calculer le score = nombre de réponses "Oui"
        int score = (int) reponses.stream().filter(r -> r).count();
        log.info("Score calculé : {}/{}", score, reponses.size());

        // 3. Classifier le niveau de risque
        NiveauRisque niveau = classifierRisque(score);
        log.info("Niveau de risque : {}", niveau);

        // 4. Sauvegarder le Résultat en DB
        Resultat resultat = new Resultat();
        resultat.setEnfant(enfant);
        resultat.setScore(score);
        resultat.setNiveauRisque(niveau);
        resultat.setDateEvaluation(LocalDate.now());
        resultatRepository.save(resultat);
        log.info("Résultat sauvegardé avec id: {}", resultat.getId());

        // 5. Générer le plan personnalisé via Claude AI
        String contenuPlan = claudeAIService.genererPlanPersonnalise(
                resultat, questions, reponses
        );

        // 6. Sauvegarder le Plan en DB
        PlanPersonnalise plan = new PlanPersonnalise();
        plan.setResultat(resultat);
        plan.setTitre("Plan d'accompagnement - Niveau " + niveau + " (Score " + score + ")");
        plan.setDescription(contenuPlan);
        plan.setObjectifs("Objectifs générés automatiquement par Claude AI");
        plan.setDateGeneration(LocalDate.now());
        planRepository.save(plan);
        log.info("Plan personnalisé sauvegardé pour résultat id: {}", resultat.getId());

        return resultat;
    }

    /**
     * Consulter tous les résultats d'un enfant (du plus récent au plus ancien).
     */
    public List<Resultat> consulterResultats(Long enfantId) {
        return resultatRepository.findByEnfantIdOrderByDateEvaluationDesc(enfantId);
    }

    /**
     * Consulter le plan personnalisé d'un résultat.
     */
    public PlanPersonnalise consulterPlan(Long resultatId) {
        return planRepository.findByResultatId(resultatId)
                .orElseThrow(() -> new RuntimeException("Plan introuvable pour résultat id: " + resultatId));
    }

    /**
     * Classification du niveau de risque selon le score.
     * Score 0-3  → FAIBLE
     * Score 4-7  → MOYEN
     * Score 8+   → ELEVE
     */
    private NiveauRisque classifierRisque(int score) {
        if (score <= 3)      return NiveauRisque.FAIBLE;
        else if (score <= 7) return NiveauRisque.MOYEN;
        else                 return NiveauRisque.ELEVE;
    }
}