package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.dto.SuiviConseilsRequest;
import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.*;
import com.autiguide.autiguide.service.ClaudeAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ParentAIController {

    private final ClaudeAIService claudeAIService;
    private final SuiviJournalierRepository suiviRepository;
    private final ResultatRepository resultatRepository;
    private final EnfantRepository enfantRepository;
    private final ParentRepository parentRepository;

    /**
     * Endpoint 1 : Enregistrer suivi journalier + Générer conseils IA.
     *
     * URL : POST /api/parent/suivi
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "score": 6,
     *   "sommeil": 2,
     *   "comportement": 3,
     *   "communication": 2,
     *   "crises": 1
     * }
     */
    @PostMapping("/suivi")
    public ResponseEntity<AIResponse> enregistrerSuiviAvecConseils(
            @RequestBody SuiviConseilsRequest request) {

        // Récupération de l'enfant
        Enfant enfant = enfantRepository
                .findById(request.getEnfantId())
                .orElseThrow(() -> new RuntimeException("Enfant introuvable"));

        // Récupération du parent de l'enfant
        Parent parent = enfant.getParent();

        // Sauvegarde du suivi journalier en DB
        SuiviJournalier suivi = new SuiviJournalier();
        suivi.setEnfant(enfant);
        suivi.setParent(parent);
        suivi.setDate(LocalDate.now());
        suivi.setQualiteSommeil(request.getSommeil());
        suivi.setNiveauComportement(request.getComportement());
        suivi.setNiveauCommunication(request.getCommunication());
        suivi.setNombreCrises(request.getCrises());
        suiviRepository.save(suivi);

        // Détermination du niveau selon le score
        NiveauRisque niveau;
        if (request.getScore() <= 3)      niveau = NiveauRisque.FAIBLE;
        else if (request.getScore() <= 7) niveau = NiveauRisque.MOYEN;
        else                              niveau = NiveauRisque.ELEVE;

        // Génération des conseils via Claude IA
        String conseils = claudeAIService.genererConseilsSuivi(
                request.getScore(),
                niveau,
                request.getSommeil(),
                request.getComportement(),
                request.getCommunication(),
                request.getCrises()
        );

        log.info("Suivi enregistré et conseils générés pour enfant id: {}",
                request.getEnfantId());

        return ResponseEntity.ok(new AIResponse(
                niveau.toString(),
                String.valueOf(request.getScore()),
                conseils,
                "✅ Suivi enregistré et conseils générés"
        ));
    }

    /**
     * Endpoint 2 : Consulter l'historique des suivis d'un enfant.
     * URL : GET /api/parent/suivi/enfant/{enfantId}
     */
    @GetMapping("/suivi/enfant/{enfantId}")
    public ResponseEntity<?> consulterHistorique(
            @PathVariable Long enfantId) {

        List<SuiviJournalier> suivis = suiviRepository
                .findByEnfantId(enfantId);

        return ResponseEntity.ok(suivis);
    }

    /**
     * Endpoint 3 : Lister les enfants d'un parent.
     * URL : GET /api/parent/{parentId}/enfants
     */
    @GetMapping("/{parentId}/enfants")
    public ResponseEntity<?> listerEnfants(
            @PathVariable Long parentId) {

        List<Enfant> enfants = enfantRepository
                .findByParentId(parentId);

        return ResponseEntity.ok(enfants);
    }
}