package com.autiguide.autiguide.dto;

import lombok.Data;

/**
 * DTO représentant une question générée par l'IA.
 * Utilisé pour transférer les données entre le service et le controller.
 */
@Data
public class QuestionDTO {
    // Numéro d'ordre de la question
    private int ordre;

    // Texte de la question
    private String contenu;

    // Domaine : communication, social, comportement, sensorialité
    private String domaine;
}