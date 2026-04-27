package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Classe représentant un enfant dans l'application.
 * Chaque enfant est associé à un parent.
 * Les questionnaires et suivis sont liés à l'enfant.
 */
@Entity
@Table(name = "enfants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Prénom de l'enfant
    private String prenom;

    // Date de naissance - utilisée pour calculer l'âge
    private LocalDate dateNaissance;

    // Sexe de l'enfant : "M" ou "F"
    private String sexe;

    /**
     * Relation ManyToOne : plusieurs enfants peuvent appartenir à un même parent.
     * @JoinColumn : crée la colonne "parent_id" dans la table "enfants"
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    /**
     * Liste des résultats obtenus pour cet enfant.
     * Un enfant peut passer plusieurs questionnaires.
     */
    @OneToMany(mappedBy = "enfant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resultat> resultats;

    /**
     * Liste des suivis journaliers concernant cet enfant.
     */
    @OneToMany(mappedBy = "enfant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SuiviJournalier> suivis;

    /**
     * Méthode utilitaire pour calculer l'âge de l'enfant.
     * Utilise Period.between() pour calculer la différence entre
     * la date de naissance et aujourd'hui.
     *
     * @return l'âge en années
     */
    public int calculerAge() {
        return Period.between(this.dateNaissance, LocalDate.now()).getYears();
    }
}
