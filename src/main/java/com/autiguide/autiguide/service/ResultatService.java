package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import com.autiguide.autiguide.exception.ResourceNotFoundException;

/**
 * Service responsable du calcul du score TSA (logique Java pure),
 * de la classification du niveau de risque selon la tranche d'âge,
 * et de la génération du plan personnalisé via IA.
 *
 * RÈGLE : Le score et le niveau sont calculés en Java — jamais par l'IA.
 * L'IA est utilisée UNIQUEMENT pour générer le plan personnalisé.
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
     * Calcule le score (Java pur), classifie le risque selon la tranche d'âge,
     * sauvegarde le résultat ET génère le plan IA adapté à l'âge.
     *
     * @param enfantId  ID de l'enfant
     * @param questions liste des questions posées
     * @param reponses  liste des réponses (true=Oui, false=Non)
     * @param age       âge de l'enfant (optionnel, calculé depuis la DB si null)
     * @return Resultat sauvegardé avec plan généré
     */
    public Resultat calculerEtSauvegarder(
            Long enfantId,
            List<String> questions,
            List<Boolean> reponses,
            Integer age) {

        // 1. Récupérer l'enfant
        Enfant enfant = enfantRepository.findById(enfantId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", enfantId));

        // 2. Calculer l'âge (depuis le paramètre ou depuis la DB)
        int ageEnfant = (age != null) ? age : enfant.calculerAge();
        log.info("Âge de l'enfant : {} ans", ageEnfant);

        // 3. Calculer le score = nombre de réponses "Non" (false)
        //    Chaque "Non" = 1 point de risque (comportement absent = signe d'alerte)
        int score = (int) reponses.stream().filter(r -> !r).count();
        log.info("Score calculé : {}/{} (nombre de Non)", score, reponses.size());

        // 4. Classifier le niveau de risque selon la tranche d'âge
        NiveauRisque niveau = classifierRisqueParTranche(score, ageEnfant);
        log.info("Niveau de risque : {} (tranche {})", niveau, determinerTranche(ageEnfant));

        // 5. Sauvegarder le Résultat en DB
        Resultat resultat = new Resultat();
        resultat.setEnfant(enfant);
        resultat.setScore(score);
        resultat.setNiveauRisque(niveau);
        resultat.setDateEvaluation(LocalDate.now());
        resultatRepository.save(resultat);
        log.info("Résultat sauvegardé avec id: {}", resultat.getId());

        // 6. Générer le plan personnalisé via IA (adapté à la tranche d'âge)
        String contenuPlan = claudeAIService.genererPlanPersonnalise(
                resultat, questions, reponses, ageEnfant
        );

        // 7. Sauvegarder le Plan en DB
        String tranche = determinerTranche(ageEnfant);
        PlanPersonnalise plan = new PlanPersonnalise();
        plan.setResultat(resultat);
        plan.setTitre("Plan d'accompagnement - " + tranche + " - Niveau " + niveau);
        plan.setDescription(contenuPlan);
        plan.setObjectifs("Plan généré par IA pour tranche " + tranche);
        plan.setDateGeneration(LocalDate.now());
        planRepository.save(plan);
        log.info("Plan personnalisé sauvegardé pour résultat id: {}", resultat.getId());

        return resultat;
    }

    /**
     * Surcharge pour compatibilité avec l'ancien code (sans âge).
     */
    public Resultat calculerEtSauvegarder(
            Long enfantId,
            List<String> questions,
            List<Boolean> reponses) {
        return calculerEtSauvegarder(enfantId, questions, reponses, null);
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
                .orElseThrow(() -> new ResourceNotFoundException("PlanPersonnalise", resultatId));
    }

    /**
     * Classification du niveau de risque selon la tranche d'âge.
     *
     * Tranche 0-3 ans (8 questions) :
     *   score 0-2 → FAIBLE | score 3-5 → MOYEN | score 6-8 → ELEVE
     *
     * Tranche 3-7 ans (10 questions) :
     *   score 0-3 → FAIBLE | score 4-6 → MOYEN | score 7-10 → ELEVE
     *
     * Tranche 7-12 ans (8 questions) :
     *   score 0-2 → FAIBLE | score 3-5 → MOYEN | score 6-8 → ELEVE
     */
    private NiveauRisque classifierRisqueParTranche(int score, int age) {
        if (age < 3) {
            // Tranche 0-3 ans — 8 questions
            if (score <= 2) return NiveauRisque.FAIBLE;
            if (score <= 5) return NiveauRisque.MOYEN;
            return NiveauRisque.ELEVE;
        } else if (age < 7) {
            // Tranche 3-7 ans — 10 questions
            if (score <= 3) return NiveauRisque.FAIBLE;
            if (score <= 6) return NiveauRisque.MOYEN;
            return NiveauRisque.ELEVE;
        } else {
            // Tranche 7-12 ans — 8 questions
            if (score <= 2) return NiveauRisque.FAIBLE;
            if (score <= 5) return NiveauRisque.MOYEN;
            return NiveauRisque.ELEVE;
        }
    }

    /**
     * Retourne le libellé de la tranche d'âge.
     */
    public static String determinerTranche(int age) {
        if (age < 3)  return "Nourrisson 0-3 ans";
        if (age < 7)  return "Préscolaire 3-7 ans";
        return "Scolaire 7-12 ans";
    }
}
