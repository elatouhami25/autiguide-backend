package com.autiguide.autiguide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse standard pour toutes les requêtes IA.
 * Retourné par tous les endpoints AI.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    // Niveau de risque : FAIBLE / MOYEN / ELEVE
    private String niveau;

    // Score ou âge selon le contexte
    private String score;

    // Contenu généré par Claude AI
    private String contenuIA;

    // Message de statut
    private String statut;
}