package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe représentant le résultat d'un questionnaire passé par un enfant.
 * Le score est calculé automatiquement selon les réponses.
 * Le niveau de risque est déterminé à partir du score.
 */
@Entity
@Table(name = "resultats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Score total calculé (nombre de réponses "Oui" à risque)
    private int score;

    /**
     * Niveau de risque stocké en String dans la base de données.
     * Exemple : "FAIBLE", "MOYEN", "ELEVE"
     */
    @Enumerated(EnumType.STRING)
    private NiveauRisque niveauRisque;

    // Date du passage du questionnaire
    private LocalDate dateEvaluation;

    /**
     * L'enfant concerné par ce résultat.
     */
    @ManyToOne
    @JoinColumn(name = "enfant_id")
    private Enfant enfant;

    /**
     * Le questionnaire qui a été passé.
     */
    @ManyToOne
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    /**
     * Le plan personnalisé généré à partir de ce résultat.
     * OneToOne : un résultat → un seul plan
     */
    @OneToOne(mappedBy = "resultat", cascade = CascadeType.ALL)
    private PlanPersonnalise plan;

    /**
     * Méthode qui détermine le niveau de risque selon le score.
     * Score 0-3  → FAIBLE
     * Score 4-7  → MOYEN
     * Score 8+   → ELEVE
     *
     * @return NiveauRisque correspondant au score
     */
    public NiveauRisque classifierRisque() {
        if (score <= 3) return NiveauRisque.FAIBLE;
        else if (score <= 7) return NiveauRisque.MOYEN;
        else return NiveauRisque.ELEVE;
    }
}
