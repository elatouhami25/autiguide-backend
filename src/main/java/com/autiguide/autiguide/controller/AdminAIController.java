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

// Indique que cette classe est un contrôleur d'API REST
@RestController
// Définit l'URL de base pour toutes les routes de cette classe
@RequestMapping("/api/admin/ai")
// Génère automatiquement un constructeur pour injecter les dépendances "final"
@RequiredArgsConstructor
// Autorise les requêtes provenant de n'importe quelle origine (utile pour relier le front-end React)
@CrossOrigin(origins = "*")
// Permet d'utiliser l'objet "log" pour écrire des messages dans la console (ex: log.info, log.error)
@Slf4j
public class AdminAIController {
    
    // Injection des services nécessaires au fonctionnement du contrôleur
    // Service qui communique avec l'intelligence artificielle (Google Gemini)
    private final ClaudeAIService claudeAIService;
    // Repository pour interagir avec la table "questionnaire" dans la base de données
    private final QuestionnaireRepository questionnaireRepository;
    // Repository pour interagir avec la table "question" dans la base de données
    private final QuestionRepository questionRepository;
    // Outil pour convertir du texte JSON en objets Java (et inversement)
    private final ObjectMapper objectMapper;

    // Route GET pour générer des questions via l'IA sans les sauvegarder (Prévisualisation)
    // L'URL attend un paramètre "age" (ex: /api/admin/ai/questions/5)
    @GetMapping("/questions/{age}")
    public ResponseEntity<AIResponse> genererQuestions(@PathVariable int age) {

        // Vérification de sécurité : l'âge doit être compris entre 0 et 18 ans
        if (age < 0 || age > 18) {
            // Si l'âge est invalide, renvoyer une erreur HTTP 400 (Bad Request) avec un message
            return ResponseEntity.badRequest().body(
                    new AIResponse("", String.valueOf(age), "", "⚠️ L'âge doit être entre 0 et 18 ans")
            );
        }

        // Appel au service IA pour générer les questions au format JSON (texte brut)
        String questionsJson = claudeAIService.genererQuestionsIA(age);

        // Si tout s'est bien passé, renvoyer une réponse HTTP 200 (OK) contenant le JSON généré
        return ResponseEntity.ok(new AIResponse(
                "",
                String.valueOf(age),
                questionsJson,
                "✅ 10 questions générées pour un enfant de " + age + " ans"
        ));
    }

    // Route POST pour générer des questions via l'IA ET les sauvegarder dans la base de données
    // L'URL prend l'ID du questionnaire cible et l'âge de l'enfant
    @PostMapping("/questionnaire/{questionnaireId}/generer/{age}")
    public ResponseEntity<AIResponse> genererEtSauvegarder(
            @PathVariable Long questionnaireId,
            @PathVariable int age) {

        // 1. Chercher le questionnaire dans la base de données via son ID
        // Si on ne le trouve pas, on lance une exception (erreur) qui arrêtera le processus
        Questionnaire questionnaire = questionnaireRepository
                .findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire introuvable"));

        // 2. Faire appel à l'IA pour générer les questions (sous forme de texte JSON)
        String questionsJson = claudeAIService.genererQuestionsIA(age);

        // 3. Essayer de traiter le JSON et de le sauvegarder (bloc try/catch pour gérer les erreurs)
        try {
            // Demander à ObjectMapper de convertir le texte JSON brut en une Liste d'objets (Map) compréhensibles par Java
            List<Map> questions = objectMapper.readValue(questionsJson, List.class);

            // Boucler sur chaque question extraite du JSON
            for (Map q : questions) {
                // Créer une nouvelle entité "Question" vide
                Question question = new Question();
                // Remplir le contenu de la question en le récupérant depuis le JSON
                question.setContenu(q.get("contenu").toString());
                // Remplir l'ordre (le numéro) de la question
                question.setOrdre((Integer) q.get("ordre"));
                // Lier cette question au questionnaire parent qu'on a récupéré au début
                question.setQuestionnaire(questionnaire);
                // Sauvegarder définitivement cette question dans la base de données
                questionRepository.save(question);
            }

            // Si la boucle s'est terminée sans problème, renvoyer un message de succès 
            return ResponseEntity.ok(new AIResponse(
                    "",
                    String.valueOf(age),
                    questionsJson,
                    "✅ " + questions.size() + " questions sauvegardées en DB"
            ));

        // Si une erreur survient (par exemple, si l'IA a mal formaté son JSON)
        } catch (Exception e) {
            // Afficher l'erreur dans la console du serveur pour pouvoir la déboguer
            log.error("Erreur parsing JSON : {}", e.getMessage());
            // Renvoyer un message d'avertissement au client (le Front-end)
            return ResponseEntity.ok(new AIResponse(
                    "", String.valueOf(age), questionsJson,
                    "⚠️ Questions générées mais non sauvegardées : " + e.getMessage()
            ));
        }
    }

    // Route GET pour lister tous les questionnaires existants dans la base de données
    @GetMapping("/questionnaires")
    public ResponseEntity<?> listerQuestionnaires() {
        // Appelle le repository pour récupérer tous les enregistrements et les renvoie avec un statut HTTP 200 OK
        return ResponseEntity.ok(questionnaireRepository.findAll());
    }
}