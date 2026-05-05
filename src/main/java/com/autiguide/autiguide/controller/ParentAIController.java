package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.dto.SuiviConseilsRequest;
import com.autiguide.autiguide.entity.SuiviJournalier;
import com.autiguide.autiguide.service.SuiviJournalierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.autiguide.autiguide.exception.ResourceNotFoundException;

/**
 * Controller pour la gestion du suivi journalier
 * et la génération des conseils IA pour les parents.
 */
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParentAIController {

    private final SuiviJournalierService suiviService;

    /**
     * POST /api/parent/suivi
     * Enregistre le suivi + génère les conseils IA.
     *
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "sommeil": 2,
     *   "comportement": 3,
     *   "communication": 2,
     *   "crises": 1
     * }
     */
    @PostMapping("/suivi")
    public ResponseEntity<AIResponse> enregistrerSuivi(
            @RequestBody SuiviConseilsRequest request) {

        // Délégation au service
        String conseils = suiviService.enregistrerEtGenererConseils(
                request.getEnfantId(),
                request.getSommeil(),
                request.getComportement(),
                request.getCommunication(),
                request.getCrises(),
                null // notes optionnelles
        );

        return ResponseEntity.ok(new AIResponse(
                "",
                String.valueOf(request.getScore()),
                conseils,
                "✅ Suivi enregistré en DB + conseils générés"
        ));
    }

    /**
     * GET /api/parent/suivi/enfant/{enfantId}
     * Consulter l'historique des suivis d'un enfant.
     */
    @GetMapping("/suivi/enfant/{enfantId}")
    public ResponseEntity<?> consulterHistorique(
            @PathVariable Long enfantId) {

        List<SuiviJournalier> suivis = suiviService.consulterHistorique(enfantId);
        return ResponseEntity.ok(suivis);
    }
}