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

@Service
@Slf4j
public class ClaudeAIService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.model}")
    private String model;

    // RestTemplate : pas besoin de dependency supplémentaire
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
            Réponds UNIQUEMENT en JSON valide :
            [{"ordre": 1, "contenu": "Question ?", "domaine": "communication"}]
            """.formatted(ageEnfant, trancheAge);

        log.info("Génération questions IA pour âge {} ans", ageEnfant);
        return appellerClaude(prompt);
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
            Tu es un expert TSA.
            Enfant de %d ans - Score : %d/10 - Niveau : %s
            Réponses : %s
            Génère un plan d'accompagnement en français avec :
            1. Analyse du résultat
            2. Objectifs prioritaires
            3. Conseils pratiques quotidiens
            4. Activités recommandées
            5. Plan hebdomadaire
            6. Quand consulter un spécialiste
            ⚠️ Ce n'est pas un diagnostic médical.
            """.formatted(age, score, niveau, reponsesFormatees);

        log.info("Génération plan - score:{} niveau:{}", score, niveau);
        return appellerClaude(prompt);
    }

    // ════════════════════════════════
    // 3. Générer Conseils Suivi
    // ════════════════════════════════
    public String genererConseilsSuivi(
            int score, NiveauRisque niveau,
            int sommeil, int comportement,
            int communication, int crises) {

        String prompt = """
            Tu es un expert en accompagnement TSA.
            Score : %d/10 - Niveau : %s
            Sommeil : %d/5 - Comportement : %d/5
            Communication : %d/5 - Crises : %d
            Génère en français :
            1. Analyse de la journée
            2. Conseils immédiats
            3. Routine du soir
            4. Activités pour demain
            5. Message d'encouragement pour les parents
            """.formatted(score, niveau, sommeil, comportement, communication, crises);

        log.info("Génération conseils suivi - score:{}", score);
        return appellerClaude(prompt);
    }

    // ════════════════════════════════
    // Appel API Claude avec RestTemplate
    // ════════════════════════════════
    private String appellerClaude(String prompt) {
        try {
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            // Body
            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", 1500,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Appel HTTP POST
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // Extraction de la réponse
            List<Map> content = (List<Map>) response.getBody().get("content");
            String texte = content.get(0).get("text").toString();
            log.info("Réponse Claude reçue ✅");
            return texte;

        } catch (Exception e) {
            log.error("Erreur Claude API : {}", e.getMessage());
            // Mock si pas de clé API
            return mockReponse(prompt);
        }
    }

    // ════════════════════════════════
    // Mock pour tester sans API Key
    // ════════════════════════════════
    private String mockReponse(String prompt) {
        if (prompt.contains("questions") || prompt.contains("JSON")) {
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
                
                🎯 OBJECTIFS PRIORITAIRES
                1. Améliorer le contact visuel
                2. Stimuler la communication verbale
                3. Encourager les interactions sociales
                
                💡 CONSEILS PRATIQUES
                - Parlez lentement et clairement
                - Utilisez des images pour communiquer
                - Créez une routine stable et prévisible
                - Encouragez le jeu avec d'autres enfants
                - Consultez un orthophoniste
                
                ⚠️ Ce résultat n'est pas un diagnostic médical officiel.
                """;
        }
        return """
            📊 ANALYSE DE LA JOURNÉE
            La journée a été moyenne avec quelques difficultés.
            
            💡 CONSEILS IMMÉDIATS
            1. Proposez des activités calmes
            2. Évitez les stimulations excessives
            3. Maintenez la routine habituelle
            
            💪 MESSAGE D'ENCOURAGEMENT
            Vous faites un excellent travail ! Continuez à observer et noter les progrès.
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