package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Question;
import com.autiguide.autiguide.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionController {

    private final QuestionService questionService;

    // ── POST /api/question/questionnaire/{questionnaireId} ──
    // Ajouter une question à un questionnaire
    @PostMapping("/questionnaire/{questionnaireId}")
    public ResponseEntity<Question> creer(
            @PathVariable Long questionnaireId,
            @RequestBody Question question) {
        return ResponseEntity.ok(questionService.sauvegarder(questionnaireId, question));
    }

    // ── GET /api/question/{id} ──
    // Trouver une question par id
    @GetMapping("/{id}")
    public ResponseEntity<Question> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.trouverParId(id));
    }

    // ── GET /api/question/questionnaire/{questionnaireId} ──
    // Lister toutes les questions d'un questionnaire
    @GetMapping("/questionnaire/{questionnaireId}")
    public ResponseEntity<List<Question>> trouverParQuestionnaire(
            @PathVariable Long questionnaireId) {
        return ResponseEntity.ok(questionService.trouverParQuestionnaire(questionnaireId));
    }

    // ── PUT /api/question/{id} ──
    // Modifier une question
    @PutMapping("/{id}")
    public ResponseEntity<Question> modifier(
            @PathVariable Long id,
            @RequestBody Question question) {
        return ResponseEntity.ok(questionService.modifier(id, question));
    }

    // ── DELETE /api/question/{id} ──
    // Supprimer une question
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        questionService.supprimer(id);
        return ResponseEntity.ok("Question supprimée avec succès");
    }
}