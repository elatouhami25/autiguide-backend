package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Questionnaire;
import com.autiguide.autiguide.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    // ── POST /api/questionnaire ──
    // Créer un questionnaire
    @PostMapping
    public ResponseEntity<Questionnaire> creer(@RequestBody Questionnaire questionnaire) {
        return ResponseEntity.ok(questionnaireService.sauvegarder(questionnaire));
    }

    // ── GET /api/questionnaire ──
    // Lister tous les questionnaires
    @GetMapping
    public ResponseEntity<List<Questionnaire>> listerTous() {
        return ResponseEntity.ok(questionnaireService.tousLesQuestionnaires());
    }

    // ── GET /api/questionnaire/{id} ──
    // Trouver un questionnaire par id
    @GetMapping("/{id}")
    public ResponseEntity<Questionnaire> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(questionnaireService.trouverParId(id));
    }

    // ── GET /api/questionnaire/age/{age} ──
    // Trouver questionnaires adaptés à l'âge de l'enfant
    @GetMapping("/age/{age}")
    public ResponseEntity<List<Questionnaire>> trouverParAge(@PathVariable int age) {
        return ResponseEntity.ok(questionnaireService.trouverParAge(age));
    }

    // ── PUT /api/questionnaire/{id} ──
    // Modifier un questionnaire
    @PutMapping("/{id}")
    public ResponseEntity<Questionnaire> modifier(
            @PathVariable Long id,
            @RequestBody Questionnaire questionnaire) {
        return ResponseEntity.ok(questionnaireService.modifier(id, questionnaire));
    }

    // ── DELETE /api/questionnaire/{id} ──
    // Supprimer un questionnaire
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        questionnaireService.supprimer(id);
        return ResponseEntity.ok("Questionnaire supprimé avec succès");
    }
}