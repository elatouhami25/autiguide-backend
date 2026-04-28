package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.NiveauRisque;
import com.autiguide.autiguide.entity.Resultat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

/**
 * Service IA utilisant Google Gemini API (gratuit).
 * Remplace Anthropic Claude API.
 */
@Service
@Slf4j
public class ClaudeAIService {

    // Clé API Google Gemini
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // URL API Gemini
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // ════════════════════════════════
    // 1. Générer Questions par IA
    // ════════════════════════════════
    public String genererQuestionsIA(int ageEnfant) {
        String trancheAge = determinerTrancheAge(ageEnfant);
        String prompt = """
            Tu es un expert en dépistage TSA basé sur le DSM-5.
            Génère exactement 10 questions adaptées à un enfant de %d ans (%s).
            Règles :
            - Réponse Oui ou Non uniquement
            - Language simple pour les parents
            - Couvrir : communication, social, comportements répétitifs, sensorialité
            Réponds UNIQUEMENT en JSON valide sans texte avant ou après :
            [{"ordre": 1, "contenu": "Question ?", "domaine": "communication"}]
            """.formatted(ageEnfant, trancheAge);

        log.info("Génération questions IA pour âge {} ans", ageEnfant);
        return appellerGemini(prompt);
    }

    // ════════════════════════════════
    // 2. Générer Plan Personnalisé
    // ════════════════════════════════
    public String genererPlanPersonnalise(
            Resultat resultat,
            List<String> questions,
            List<Boolean> reponses) {

        int age = resultat.getEnfant().calculerAge();
        int score = resultat.getScore();
        NiveauRisque niveau = resultat.getNiveauRisque();

        StringBuilder reponsesFormatees = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            reponsesFormatees
                    .append("- ").append(questions.get(i))
                    .append(" → ").append(reponses.get(i) ? "Oui" : "Non")
                    .append("\n");
        }

        String prompt = """
            Tu es un expert TSA (Trouble du Spectre Autistique).
            Enfant de %d ans - Score : %d/10 - Niveau : %s
            Réponses aux questions :
            %s
            Génère un plan d'accompagnement personnalisé en français avec :
            1. Analyse du résultat (explication pour les parents)
            2. Objectifs prioritaires (3 objectifs)
            3. Conseils pratiques quotidiens (5 conseils)
            4. Activités recommandées (4 activités)
            5. Plan hebdomadaire simple
            6. Quand consulter un spécialiste
            ⚠️ Rappelle que ce n'est pas un diagnostic médical officiel.
            Utilise un ton bienveillant et encourageant.
            """.formatted(age, score, niveau, reponsesFormatees);

        log.info("Génération plan - score:{} niveau:{}", score, niveau);
        return appellerGemini(prompt);
    }

    // ════════════════════════════════
    // 3. Générer Conseils Suivi
    // ════════════════════════════════
    public String genererConseilsSuivi(
            int score, NiveauRisque niveau,
            int sommeil, int comportement,
            int communication, int crises) {

        String prompt = """
            Tu es un expert en accompagnement des enfants TSA.
            Score TSA : %d/10 - Niveau : %s
            Indicateurs du jour :
            - Sommeil : %d/5
            - Comportement : %d/5
            - Communication : %d/5
            - Nombre de crises : %d
            Génère en français :
            1. Analyse de la journée (points positifs + points à améliorer)
            2. Conseils immédiats (3 conseils pratiques)
            3. Routine du soir recommandée
            4. Activités pour demain (3 activités adaptées)
            5. Message d'encouragement pour les parents
            Utilise des emojis et un ton chaleureux.
            """.formatted(score, niveau, sommeil, comportement, communication, crises);

        log.info("Génération conseils suivi - score:{}", score);
        return appellerGemini(prompt);
    }

    // ════════════════════════════════
    // Appel API Google Gemini
    // ════════════════════════════════
    private String appellerGemini(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Format du body Gemini
            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            // URL avec API Key en paramètre
            String url = geminiApiUrl + "?key=" + geminiApiKey;

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Appel HTTP POST
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // Extraction de la réponse Gemini
            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            String texte = parts.get(0).get("text").toString();

            log.info("Réponse Gemini reçue avec succès ✅");
            return texte;

        } catch (Exception e) {
            log.error("Erreur Gemini API : {}", e.getMessage());
            return mockReponse(prompt);
        }
    }

    // ════════════════════════════════
    // Mock si API indisponible
    // ════════════════════════════════
    private String mockReponse(String prompt) {
        if (prompt.contains("JSON")) {
            return """
                [
                  {"ordre": 1, "contenu": "Votre enfant établit-il un contact visuel ?", "domaine": "communication"},
                  {"ordre": 2, "contenu": "Répond-il quand on l'appelle par son prénom ?", "domaine": "communication"},
                  {"ordre": 3, "contenu": "Pointe-t-il du doigt pour montrer quelque chose ?", "domaine": "social"},
                  {"ordre": 4, "contenu": "Joue-t-il à faire semblant ?", "domaine": "social"},
                  {"ordre": 5, "contenu": "S'intéresse-t-il aux autres enfants ?", "domaine": "social"},
                  {"ordre": 6, "contenu": "Imite-t-il les gestes des autres ?", "domaine": "communication"},
                  {"ordre": 7, "contenu": "A-t-il des comportements répétitifs ?", "domaine": "comportement"},
                  {"ordre": 8, "contenu": "Est-il hypersensible aux bruits ou textures ?", "domaine": "sensorialité"},
                  {"ordre": 9, "contenu": "A-t-il un vocabulaire adapté à son âge ?", "domaine": "communication"},
                  {"ordre": 10, "contenu": "Comprend-il les consignes simples ?", "domaine": "communication"}
                ]
                """;
        }
        if (prompt.contains("plan")) {
            return """
                📋 ANALYSE DU RÉSULTAT
                Votre enfant présente quelques signes qui méritent attention.
                🎯 OBJECTIFS : Améliorer contact visuel, stimuler communication, interactions sociales.
                💡 CONSEILS : Routine stable, images pour communiquer, jeux avec d'autres enfants.
                ⚠️ Ce résultat n'est pas un diagnostic médical officiel.
                """;
        }
        return """
            📊 Journée moyenne avec quelques difficultés.
            💡 Conseils : activités calmes, éviter stimulations excessives, maintenir la routine.
            💪 Vous faites un excellent travail !
            """;
    }

    // ════════════════════════════════
    // Méthodes utilitaires
    // ════════════════════════════════
    private String determinerTrancheAge(int age) {
        if (age <= 2)       return "0-2 ans";
        else if (age <= 4)  return "2-4 ans";
        else if (age <= 6)  return "4-6 ans";
        else if (age <= 10) return "6-10 ans";
        else                return "10+ ans";
    }
}