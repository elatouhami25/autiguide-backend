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
 * Service IA utilisant Google Gemini API.
 *
 * L'IA est utilisée UNIQUEMENT pour :
 * 1. Générer le plan personnalisé (adapté à la tranche d'âge)
 * 2. Générer les conseils du suivi journalier (adaptés à l'âge)
 *
 * L'IA NE génère PAS les questions (fixes en DB).
 * L'IA NE calcule PAS le score (logique Java pure dans ResultatService).
 */
@Service
@Slf4j
public class ClaudeAIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // ════════════════════════════════════════════════════════════════════════
    // 1. Générer le plan personnalisé — adapté à la tranche d'âge
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Génère un plan personnalisé adapté à la tranche d'âge de l'enfant.
     *
     * @param resultat  résultat du questionnaire (score + niveau)
     * @param questions liste des questions posées
     * @param reponses  liste des réponses (true=Oui, false=Non)
     * @param age       âge de l'enfant en années
     * @return plan personnalisé en texte
     */
    public String genererPlanPersonnalise(
            Resultat resultat,
            List<String> questions,
            List<Boolean> reponses,
            int age) {

        int score = resultat.getScore();
        NiveauRisque niveau = resultat.getNiveauRisque();

        // Construire le résumé des réponses
        StringBuilder reponsesFormatees = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            reponsesFormatees
                    .append("- ").append(questions.get(i))
                    .append(" → ").append(reponses.get(i) ? "Oui" : "Non")
                    .append("\n");
        }

        // Construire le prompt adapté à la tranche d'âge
        String prompt = construirePromptPlan(age, score, niveau, reponsesFormatees.toString());

        log.info("Génération plan IA - âge:{} score:{} niveau:{}", age, score, niveau);
        return appellerGemini(prompt);
    }

    /**
     * Surcharge pour compatibilité avec l'ancien code (sans âge explicite).
     */
    public String genererPlanPersonnalise(
            Resultat resultat,
            List<String> questions,
            List<Boolean> reponses) {
        int age = resultat.getEnfant().calculerAge();
        return genererPlanPersonnalise(resultat, questions, reponses, age);
    }

    /**
     * Construit le prompt COMPLET et structuré adapté à la tranche d'âge.
     * Le plan doit être concret, applicable, avec exemples réels.
     */
    private String construirePromptPlan(int age, int score, NiveauRisque niveau, String reponses) {

        String trancheLabel = age < 3 ? "Nourrisson/Petite enfance (0-3 ans)"
                            : age < 7 ? "Préscolaire (3-7 ans)"
                            : "Scolaire (7-12 ans)";

        String focusSpecifique = age < 3
            ? """
              - Stimulation sensorielle (toucher, vue, ouïe)
              - Renforcement de l'attachement parent-enfant
              - Développement moteur et psychomoteur
              - Encouragement des premiers mots et sons
              - Routines simples et prévisibles
              - Jeux sensori-moteurs adaptés
              """
            : age < 7
            ? """
              - Développement du jeu symbolique et imaginaire
              - Enrichissement du langage oral
              - Gestion des émotions (nommer, exprimer)
              - Compétences sociales de base (partager, attendre son tour)
              - Routine structurée et visuelle
              - Activités sensorielles adaptées
              """
            : """
              - Habiletés sociales avancées (conversations, amitié)
              - Communication verbale et non-verbale
              - Gestion des transitions et changements
              - Autonomie scolaire et organisation
              - Compréhension des règles sociales implicites
              - Techniques de gestion du stress et des émotions
              """;

        return """
            Tu es un expert en accompagnement des enfants avec TSA (Trouble du Spectre Autistique).
            
            PROFIL DE L'ENFANT :
            - Âge : %d ans — Tranche : %s
            - Score TSA : %d points de risque
            - Niveau de risque : %s
            
            Réponses au questionnaire :
            %s
            
            MISSION : Génère un plan d'accompagnement COMPLET, STRUCTURÉ et TRÈS CONCRET
            pour aider les parents à accompagner leur enfant au quotidien.
            
            STRUCTURE OBLIGATOIRE (respecte exactement ces 7 sections) :
            
            📊 1. ANALYSE SIMPLE ET CLAIRE
            - Explication du comportement de l'enfant (langage simple pour les parents)
            - Points de vigilance identifiés
            - Points positifs à valoriser
            
            🏠 2. CONSEILS PRATIQUES À LA MAISON
            - Organisation de la journée (routine avec horaires)
            - Gestion des crises (étapes concrètes)
            - Communication avec l'enfant (phrases exactes à utiliser)
            - Exemples concrets : "Quand votre enfant fait X, dites Y"
            
            🎯 3. ACTIVITÉS ET JEUX ADAPTÉS À L'ÂGE (%d ans)
            Donne 4 activités PRÉCISES avec pour chacune :
            • Nom de l'activité
            • Objectif (communication / concentration / interaction sociale)
            • Comment jouer (étapes simples)
            • Durée recommandée
            Focus sur : %s
            
            👥 4. INTERACTION SOCIALE
            - Comment aider l'enfant à se faire des amis
            - Exercices simples (ex: dire bonjour, attendre son tour, partager)
            - Mise en situation avec exemples de dialogue
            
            🏫 5. CONSEILS POUR L'ÉCOLE / LA MATERNELLE
            - Comment collaborer avec l'enseignant
            - Aménagements possibles en classe
            - Suivi du progrès à la maison
            
            🚨 6. PLAN D'ACTION EN CAS DE DIFFICULTÉ
            - Que faire en cas de crise (étapes 1-2-3)
            - Techniques de calme (respiration, espace calme)
            - Erreurs courantes à éviter absolument
            
            📅 7. MINI PLANNING HEBDOMADAIRE
            Crée un exemple de planning simple pour une semaine :
            - Lundi à Vendredi : activités + moments de repos + routines
            - Weekend : activités familiales adaptées
            
            STYLE OBLIGATOIRE :
            - Langage simple et bienveillant (compréhensible par tous les parents)
            - Titres clairs avec emojis
            - Bullet points pour chaque conseil
            - Exemples CONCRETS et RÉELS (pas de généralités)
            - Ton encourageant et positif
            
            ⚠️ Termine par : "Ce plan est un outil d'accompagnement. Il ne remplace pas un diagnostic médical. Consultez un professionnel de santé spécialisé."
            """.formatted(age, trancheLabel, score, niveau, reponses, age, focusSpecifique);
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. Générer les conseils du suivi journalier — adaptés à l'âge
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Génère des conseils personnalisés pour le suivi journalier,
     * adaptés à l'âge de l'enfant et aux indicateurs du jour.
     *
     * @param score         score TSA global de l'enfant
     * @param niveau        niveau de risque (FAIBLE/MOYEN/ELEVE)
     * @param age           âge de l'enfant (pour adapter les conseils)
     * @param sommeil       qualité du sommeil (1-5)
     * @param comportement  niveau de comportement (1-5)
     * @param communication niveau de communication (1-5)
     * @param crises        nombre de crises dans la journée
     * @return conseils personnalisés en texte
     */
    public String genererConseilsSuivi(
            int score, NiveauRisque niveau, int age,
            int sommeil, int comportement,
            int communication, int crises) {

        String focusAge = determinerFocusSuivi(age);

        String prompt = """
            Tu es un expert en accompagnement des enfants TSA.
            Enfant de %d ans — Score TSA : %d — Niveau : %s
            Indicateurs du jour :
            - Sommeil : %d/5
            - Comportement : %d/5
            - Communication : %d/5
            - Nombre de crises : %d
            Focus adapté à l'âge : %s
            Génère en français :
            1. Analyse de la journée (points positifs + points à améliorer)
            2. Conseils immédiats (3 conseils pratiques adaptés à l'âge)
            3. Routine du soir recommandée
            4. Activités pour demain (3 activités adaptées à l'âge)
            5. Message d'encouragement pour les parents
            Utilise des emojis et un ton chaleureux et bienveillant.
            """.formatted(age, score, niveau, sommeil, comportement, communication, crises, focusAge);

        log.info("Génération conseils suivi - âge:{} score:{}", age, score);
        return appellerGemini(prompt);
    }

    /**
     * Surcharge sans âge (compatibilité avec l'ancien code).
     */
    public String genererConseilsSuivi(
            int score, NiveauRisque niveau,
            int sommeil, int comportement,
            int communication, int crises) {
        // Âge par défaut : 3 ans (tranche préscolaire)
        return genererConseilsSuivi(score, niveau, 3, sommeil, comportement, communication, crises);
    }

    /**
     * Détermine le focus des conseils selon la tranche d'âge.
     */
    private String determinerFocusSuivi(int age) {
        if (age < 3)  return "routine, sommeil, stimulation sensorielle, attachement";
        if (age < 7)  return "émotions, jeu symbolique, communication, socialisation";
        return "autonomie, règles sociales, école, gestion du stress";
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. Générer des questions par IA (conservé pour compatibilité admin)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Génère des questions par IA (utilisé uniquement par l'admin).
     * Les questions du questionnaire parent sont fixes en DB.
     */
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

        log.info("Génération questions IA pour âge {} ans (admin)", ageEnfant);
        return appellerGemini(prompt);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Appel API Google Gemini
    // ════════════════════════════════════════════════════════════════════════

    private String appellerGemini(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            String url = geminiApiUrl + "?key=" + geminiApiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class
            );

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

    // ════════════════════════════════════════════════════════════════════════
    // Mock si API indisponible
    // ════════════════════════════════════════════════════════════════════════

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

        // Plan complet structuré (mock riche)
        if (prompt.contains("Scolaire") || prompt.contains("7-12")) {
            return """
                📊 1. ANALYSE SIMPLE ET CLAIRE

                Votre enfant présente des signes qui méritent une attention bienveillante et un accompagnement adapté.

                Points de vigilance identifiés :
                • Difficultés dans les interactions sociales avec les pairs
                • Sensibilité aux changements de routine
                • Comportements répétitifs observés

                Points positifs à valoriser :
                • Capacité de concentration sur ses centres d'intérêt
                • Mémoire souvent développée
                • Sincérité et authenticité dans les relations

                🏠 2. CONSEILS PRATIQUES À LA MAISON

                Organisation de la journée :
                • 7h00 — Réveil avec routine fixe (même ordre chaque matin)
                • 7h30 — Petit-déjeuner calme, sans écrans
                • Après l'école — 30 min de décompression avant les devoirs
                • 20h00 — Routine du soir : bain → lecture → dodo

                Gestion des crises :
                1. Restez calme et parlez doucement
                2. Proposez un espace calme ("Tu veux aller dans ta chambre 5 minutes ?")
                3. Évitez les discussions pendant la crise
                4. Après le calme, expliquez simplement ce qui s'est passé

                Communication avec l'enfant :
                • Au lieu de "Arrête ça !" → dites "Je vois que tu es frustré. Respire avec moi."
                • Au lieu de "Pourquoi tu fais ça ?" → dites "Qu'est-ce qui t'a mis en colère ?"
                • Annoncez les changements à l'avance : "Dans 10 minutes, on arrête le jeu."

                🎯 3. ACTIVITÉS ET JEUX ADAPTÉS (7-12 ans)

                Activité 1 : Jeu de rôle "La conversation"
                • Objectif : Améliorer la communication et les interactions sociales
                • Comment jouer : Simulez des situations du quotidien (commander au restaurant, demander de l'aide)
                • Durée : 15-20 minutes, 3 fois par semaine

                Activité 2 : Puzzle collaboratif
                • Objectif : Développer la concentration et la coopération
                • Comment jouer : Faites un puzzle ensemble, chacun cherche des pièces d'une couleur
                • Durée : 20-30 minutes

                Activité 3 : Jeu de cartes "Uno" ou "Memory"
                • Objectif : Apprendre à attendre son tour, gérer la frustration
                • Comment jouer : Jouez en famille avec des règles simples et claires
                • Durée : 20 minutes

                Activité 4 : Journal des émotions
                • Objectif : Identifier et exprimer ses émotions
                • Comment faire : Chaque soir, dessinez ou écrivez l'émotion du jour
                • Durée : 10 minutes avant le coucher

                👥 4. INTERACTION SOCIALE

                Comment aider votre enfant à se faire des amis :
                • Organisez des rendez-vous en petit groupe (1-2 enfants max au début)
                • Choisissez des activités structurées (jeux de société, sport)
                • Entraînez les formules sociales à la maison : "Bonjour", "Tu veux jouer ?", "C'est ton tour"

                Exercices pratiques :
                • Exercice "Bonjour" : Chaque matin, pratiquez le contact visuel + sourire + "Bonjour"
                • Exercice "Attendre son tour" : Jeux de société en famille chaque soir
                • Mise en situation : "Si un ami te prend ton jouet, que dis-tu ?"

                🏫 5. CONSEILS POUR L'ÉCOLE

                Collaboration avec l'enseignant :
                • Demandez un rendez-vous mensuel pour faire le point
                • Partagez les stratégies qui fonctionnent à la maison
                • Demandez une place en avant de la classe (moins de distractions)

                Aménagements possibles :
                • Temps supplémentaire pour les contrôles
                • Instructions écrites en plus des instructions orales
                • Espace calme disponible en cas de surcharge sensorielle

                🚨 6. PLAN D'ACTION EN CAS DE DIFFICULTÉ

                En cas de crise :
                1. Ne pas hausser la voix
                2. Réduire les stimulations (baisser la lumière, le bruit)
                3. Proposer un objet réconfortant (doudou, balle anti-stress)
                4. Attendre que la crise passe avant de parler

                Techniques de calme :
                • Respiration 4-7-8 : inspirer 4s, retenir 7s, expirer 8s
                • Espace calme avec coussin, lumière douce et musique douce
                • Activité physique courte (sauter, courir 5 minutes)

                Erreurs à éviter :
                ❌ Punir pendant la crise
                ❌ Forcer le contact physique
                ❌ Changer la routine sans prévenir

                📅 7. MINI PLANNING HEBDOMADAIRE

                Lundi : École + 30 min jeu libre + Journal des émotions
                Mardi : École + Activité physique (vélo, natation) + Lecture
                Mercredi : Activité sociale (ami à la maison) + Jeu de société en famille
                Jeudi : École + Puzzle ou jeu de construction + Routine calme
                Vendredi : École + Activité créative (dessin, musique) + Film en famille
                Samedi : Sortie en nature + Jeu de rôle + Temps libre structuré
                Dimanche : Préparation de la semaine (planning visuel) + Activité calme

                ⚠️ Ce plan est un outil d'accompagnement. Il ne remplace pas un diagnostic médical. Consultez un professionnel de santé spécialisé.
                """;
        }

        if (prompt.contains("Préscolaire") || prompt.contains("3-7")) {
            return """
                📊 1. ANALYSE SIMPLE ET CLAIRE

                Votre enfant présente des signes qui méritent une attention bienveillante.

                Points de vigilance :
                • Développement du langage à stimuler
                • Interactions sociales à encourager
                • Gestion des émotions à travailler

                Points positifs :
                • Curiosité naturelle à valoriser
                • Capacité d'apprentissage par le jeu

                🏠 2. CONSEILS PRATIQUES À LA MAISON

                Routine quotidienne :
                • Utilisez un tableau visuel avec images pour la journée
                • Annoncez chaque transition : "Dans 5 minutes, on mange"
                • Nommez les émotions : "Je vois que tu es en colère"

                🎯 3. ACTIVITÉS ADAPTÉES (3-7 ans)

                Activité 1 : Jeu de la dînette
                • Objectif : Développer le jeu symbolique et le langage
                • Comment : Faites semblant de cuisiner et manger ensemble
                • Durée : 20 minutes

                Activité 2 : Livre d'images des émotions
                • Objectif : Identifier et nommer les émotions
                • Comment : Regardez ensemble, nommez les visages
                • Durée : 10 minutes avant le coucher

                👥 4. INTERACTION SOCIALE
                • Organisez des jeux en binôme avec un enfant calme
                • Entraînez "Bonjour" et "Au revoir" chaque jour

                🏫 5. CONSEILS POUR LA MATERNELLE
                • Informez l'enseignant des besoins spécifiques
                • Demandez un coin calme disponible

                🚨 6. EN CAS DE CRISE
                1. Restez calme
                2. Proposez un câlin ou un espace calme
                3. Attendez avant de parler

                📅 7. PLANNING HEBDOMADAIRE
                Lundi-Vendredi : Routine fixe + 1 activité créative
                Weekend : Sortie nature + Jeu en famille

                ⚠️ Ce plan est un outil d'accompagnement. Consultez un professionnel de santé.
                """;
        }

        // Nourrisson 0-3 ans
        return """
            📊 1. ANALYSE SIMPLE ET CLAIRE

            Votre bébé présente des signes qui méritent une attention douce et bienveillante.

            Points de vigilance :
            • Réponse aux stimulations à observer
            • Développement du lien d'attachement à renforcer

            Points positifs :
            • Chaque interaction compte et construit le lien

            🏠 2. CONSEILS PRATIQUES À LA MAISON

            • Parlez constamment à votre bébé en nommant tout
            • Maintenez une routine prévisible (repas, bain, dodo)
            • Proposez des jeux sensoriels doux

            🎯 3. ACTIVITÉS ADAPTÉES (0-3 ans)

            Activité 1 : Jeu du miroir
            • Objectif : Développer la conscience de soi et le contact visuel
            • Comment : Faites des grimaces devant le miroir ensemble
            • Durée : 5-10 minutes

            Activité 2 : Bac sensoriel
            • Objectif : Stimulation tactile et sensorielle
            • Comment : Bac avec riz, sable, eau tiède — explorez ensemble
            • Durée : 15 minutes

            📅 7. PLANNING HEBDOMADAIRE
            Chaque jour : Routine fixe + 2 activités sensorielles + Lecture

            ⚠️ Ce plan est un outil d'accompagnement. Consultez un pédiatre ou spécialiste.
            """;
    }

    private String determinerTrancheAge(int age) {
        if (age < 3)  return "0-3 ans - Nourrisson";
        if (age < 7)  return "3-7 ans - Préscolaire";
        return "7-12 ans - Scolaire";
    }
}
