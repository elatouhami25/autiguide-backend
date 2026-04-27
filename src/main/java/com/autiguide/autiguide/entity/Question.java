package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Classe représentant une question dans un questionnaire.
 * Chaque question attend une réponse de type Oui/Non (Boolean).
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Texte de la question posée au parent
    private String contenu;

    // Numéro d'ordre d'affichage de la question
    private int ordre;

    /**
     * Le questionnaire auquel appartient cette question.
     * ManyToOne : plusieurs questions pour un seul questionnaire.
     */
    @ManyToOne
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    /**
     * Les réponses données à cette question.
     * Une même question peut avoir plusieurs réponses
     * (une par passage de questionnaire).
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reponse> reponses;
}