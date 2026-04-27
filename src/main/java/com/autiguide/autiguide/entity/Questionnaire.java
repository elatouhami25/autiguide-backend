package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe représentant un questionnaire de dépistage.
 * Chaque questionnaire est adapté à une tranche d'âge (ageMin - ageMax).
 * Il contient une liste de questions.
 */
@Entity
@Table(name = "questionnaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre du questionnaire (ex: "Questionnaire 18-24 mois")
    private String titre;

    // Âge minimum de l'enfant pour ce questionnaire (en mois ou années)
    private int ageMin;

    // Âge maximum de l'enfant pour ce questionnaire
    private int ageMax;

    // Date de création du questionnaire
    private LocalDate dateCreation;

    /**
     * Liste des questions du questionnaire.
     * Composition : les questions n'existent pas sans le questionnaire.
     * cascade = ALL : suppression du questionnaire → suppression des questions
     */
    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;
}