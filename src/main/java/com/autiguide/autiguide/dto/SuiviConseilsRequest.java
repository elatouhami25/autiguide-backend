package com.autiguide.autiguide.dto;

import lombok.Data;

/**
 * DTO pour la requête de génération des conseils du suivi journalier.
 * Le parent envoie ces données pour recevoir les conseils IA.
 */
@Data
public class SuiviConseilsRequest {
    // Score TSA global de l'enfant (0-10)
    private int score;

    // Qualité du sommeil (1=très mauvais → 5=très bon)
    private int sommeil;

    // Niveau de comportement (1=très agité → 5=très calme)
    private int comportement;

    // Niveau de communication (1=aucune → 5=très bonne)
    private int communication;

    // Nombre de crises dans la journée
    private int crises;

    // ID de l'enfant concerné
    private Long enfantId;
}