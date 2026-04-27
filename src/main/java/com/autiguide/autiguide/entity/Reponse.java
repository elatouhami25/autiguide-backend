package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe représentant la réponse donnée à une question.
 * La réponse est de type Oui (true) ou Non (false).
 * Chaque réponse est liée à une question et à un résultat global.
 */
@Entity
@Table(name = "reponses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // true = Oui / false = Non
    private Boolean valeur;

    // Date à laquelle la réponse a été donnée
    private LocalDate dateReponse;

    /**
     * La question à laquelle cette réponse correspond.
     */
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    /**
     * Le résultat global auquel cette réponse contribue.
     */
    @ManyToOne
    @JoinColumn(name = "resultat_id")
    private Resultat resultat;
}