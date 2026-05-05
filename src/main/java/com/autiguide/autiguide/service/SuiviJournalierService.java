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
 * Service responsable de l'enregistrement du suivi journalier
 * et de la génération des conseils personnalisés via IA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuiviJournalierService {

    private final SuiviJournalierRepository suiviRepository;
    private final ResultatRepository resultatRepository;
    private final EnfantRepository enfantRepository;
    private final ClaudeAIService claudeAIService;

    /**
     * Enregistre le suivi journalier en DB
     * ET génère les conseils IA selon le score TSA + les indicateurs du jour.
     *
     * @param enfantId      ID de l'enfant
     * @param sommeil       qualité du sommeil (1-5)
     * @param comportement  niveau de comportement (1-5)
     * @param communication niveau de communication (1-5)
     * @param crises        nombre de crises dans la journée
     * @param notes         notes libres du parent
     * @return conseils générés par l'IA
     */
    public String enregistrerEtGenererConseils(
            Long enfantId,
            int sommeil,
            int comportement,
            int communication,
            int crises,
            String notes) {

        // 1. Récupérer l'enfant
        Enfant enfant = enfantRepository.findById(enfantId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", enfantId));

        // 2. Récupérer le parent de l'enfant
        Parent parent = enfant.getParent();

        // 3. Sauvegarder le suivi journalier en DB
        SuiviJournalier suivi = new SuiviJournalier();
        suivi.setEnfant(enfant);
        suivi.setParent(parent);
        suivi.setDate(LocalDate.now());
        suivi.setQualiteSommeil(sommeil);
        suivi.setNiveauComportement(comportement);
        suivi.setNiveauCommunication(communication);
        suivi.setNombreCrises(crises);
        suivi.setNotes(notes);
        suiviRepository.save(suivi);
        log.info("Suivi journalier sauvegardé pour enfant id: {}", enfantId);

        // 4. Récupérer le dernier résultat TSA de l'enfant
        List<Resultat> resultats = resultatRepository
                .findByEnfantIdOrderByDateEvaluationDesc(enfantId);

        // 5. Si l'enfant a déjà passé un questionnaire → utiliser son score
        if (!resultats.isEmpty()) {
            Resultat dernierResultat = resultats.get(0);
            int score = dernierResultat.getScore();
            NiveauRisque niveau = dernierResultat.getNiveauRisque();

            log.info("Génération conseils IA - score:{} niveau:{}", score, niveau);

            // 6. Générer les conseils via Claude AI
            return claudeAIService.genererConseilsSuivi(
                    score, niveau,
                    sommeil, comportement, communication, crises
            );
        }

        // Si pas encore de résultat → conseils génériques
        log.warn("Aucun résultat TSA trouvé pour enfant id: {} - conseils génériques", enfantId);
        return claudeAIService.genererConseilsSuivi(
                5, NiveauRisque.MOYEN,
                sommeil, comportement, communication, crises
        );
    }

    /**
     * Consulter l'historique des suivis d'un enfant.
     */
    public List<SuiviJournalier> consulterHistorique(Long enfantId) {
        return suiviRepository.findByEnfantId(enfantId);
    }

    /**
     * Consulter les suivis d'un enfant entre deux dates.
     */
    public List<SuiviJournalier> consulterHistoriqueParPeriode(
            Long enfantId,
            LocalDate debut,
            LocalDate fin) {
        return suiviRepository.findByEnfantIdAndDateBetween(enfantId, debut, fin);
    }
}