package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.dto.AIResponse;
import com.autiguide.autiguide.entity.Question;
import com.autiguide.autiguide.entity.Questionnaire;
import com.autiguide.autiguide.repository.QuestionRepository;
import com.autiguide.autiguide.repository.QuestionnaireRepository;
import com.autiguide.autiguide.service.ClaudeAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AdminAIController {

    private final ClaudeAIService claudeAIService;
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    // GET /api/admin/ai/questions/{age}
    @GetMapping("/questions/{age}")
    public ResponseEntity<AIResponse> genererQuestions(@PathVariable int age) {

        if (age < 0 || age > 18) {
            return ResponseEntity.badRequest().body(
                    new AIResponse("", String.valueOf(age), "", "⚠️ L'âge doit être entre 0 et 18 ans")
            );
        }

        String questionsJson = claudeAIService.genererQuestionsIA(age);

        return ResponseEntity.ok(new AIResponse(
                "",
                String.valueOf(age),
                questionsJson,
                "✅ 10 questions générées pour un enfant de " + age + " ans"
        ));
    }

    // POST /api/admin/ai/questionnaire/{questionnaireId}/generer/{age}
    @PostMapping("/questionnaire/{questionnaireId}/generer/{age}")
    public ResponseEntity<AIResponse> genererEtSauvegarder(
            @PathVariable Long questionnaireId,
            @PathVariable int age) {

        Questionnaire questionnaire = questionnaireRepository
                .findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire introuvable"));

        String questionsJson = claudeAIService.genererQuestionsIA(age);

        try {
            List<Map> questions = objectMapper.readValue(questionsJson, List.class);

            for (Map q : questions) {
                Question question = new Question();
                question.setContenu(q.get("contenu").toString());
                question.setOrdre((Integer) q.get("ordre"));
                question.setQuestionnaire(questionnaire);
                questionRepository.save(question);
            }

            return ResponseEntity.ok(new AIResponse(
                    "",
                    String.valueOf(age),
                    questionsJson,
                    "✅ " + questions.size() + " questions sauvegardées en DB"
            ));

        } catch (Exception e) {
            log.error("Erreur parsing JSON : {}", e.getMessage());
            return ResponseEntity.ok(new AIResponse(
                    "", String.valueOf(age), questionsJson,
                    "⚠️ Questions générées mais non sauvegardées : " + e.getMessage()
            ));
        }
    }

    // GET /api/admin/ai/questionnaires
    @GetMapping("/questionnaires")
    public ResponseEntity<?> listerQuestionnaires() {
        return ResponseEntity.ok(questionnaireRepository.findAll());
    }
}