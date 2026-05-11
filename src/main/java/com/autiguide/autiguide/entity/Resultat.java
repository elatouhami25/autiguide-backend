package com.autiguide.autiguide.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
     * On ignore les champs qui créent des boucles de sérialisation.
     */
    @ManyToOne
    @JoinColumn(name = "enfant_id")
    @JsonIgnoreProperties({"resultats", "suivis", "parent"})
    private Enfant enfant;

    /**
     * Le questionnaire qui a été passé.
     */
    @ManyToOne
    @JoinColumn(name = "questionnaire_id")
    @JsonIgnoreProperties({"questions"})
    private Questionnaire questionnaire;

    /**
     * Le plan personnalisé — ignoré dans la sérialisation de Resultat
     * pour éviter les réponses trop volumineuses et les boucles infinies.
     * Utiliser GET /api/resultat/{id}/plan pour récupérer le plan séparément.
     */
    @JsonIgnore
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
