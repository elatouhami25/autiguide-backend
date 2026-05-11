package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.dto.SuiviConseilsRequest;
import com.autiguide.autiguide.entity.SuiviJournalier;
import com.autiguide.autiguide.service.SuiviJournalierService;
import com.autiguide.autiguide.repository.SuiviJournalierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST dédié au suivi journalier.
 *
 * Endpoints :
 *   POST   /api/suivi              → Enregistrer un suivi + conseils IA
 *   GET    /api/suivi/enfant/{id}  → Historique des suivis d'un enfant
 *   GET    /api/suivi/parent/{id}  → Suivis enregistrés par un parent
 *   GET    /api/suivi/{id}         → Détail d'un suivi
 *   DELETE /api/suivi/{id}         → Supprimer un suivi
 */
@RestController
@RequestMapping("/api/suivi")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuiviJournalierController {

    private final SuiviJournalierService suiviService;
    private final SuiviJournalierRepository suiviRepository;

    /**
     * POST /api/suivi
     * Enregistre le suivi journalier en DB et génère les conseils IA.
     *
     * Body JSON :
     * {
     *   "enfantId": 1,
     *   "sommeil": 4,
     *   "comportement": 3,
     *   "communication": 3,
     *   "crises": 1,
     *   "notes": "Bonne journée"
     * }
     */
    @PostMapping
    public ResponseEntity<AIResponse> enregistrerSuivi(
            @RequestBody SuiviConseilsRequest request) {

        String conseils = suiviService.enregistrerEtGenererConseils(
                request.getEnfantId(),
                request.getSommeil(),
                request.getComportement(),
                request.getCommunication(),
                request.getCrises(),
                request.getNotes()
        );

        return ResponseEntity.ok(new AIResponse(
                "",
                String.valueOf(request.getScore()),
                conseils,
                "✅ Suivi enregistré + conseils IA générés"
        ));
    }

    /**
     * GET /api/suivi/enfant/{enfantId}
     * Retourne l'historique complet des suivis d'un enfant.
     */
    @GetMapping("/enfant/{enfantId}")
    public ResponseEntity<List<SuiviJournalier>> getByEnfant(
            @PathVariable Long enfantId) {
        return ResponseEntity.ok(suiviService.consulterHistorique(enfantId));
    }

    /**
     * GET /api/suivi/parent/{parentId}
     * Retourne tous les suivis enregistrés par un parent.
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<SuiviJournalier>> getByParent(
            @PathVariable Long parentId) {
        return ResponseEntity.ok(suiviRepository.findByParentId(parentId));
    }

    /**
     * GET /api/suivi/{id}
     * Retourne le détail d'un suivi par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuiviJournalier> getById(@PathVariable Long id) {
        return suiviRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/suivi/{id}
     * Supprime un suivi journalier.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        if (!suiviRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        suiviRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Suivi supprimé avec succès"));
    }
}
