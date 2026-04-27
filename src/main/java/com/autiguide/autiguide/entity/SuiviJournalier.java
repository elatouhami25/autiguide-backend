package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe représentant le suivi quotidien de l'enfant.
 * Le parent enregistre chaque jour l'état de son enfant
 * sur plusieurs indicateurs notés de 1 à 5.
 */
@Entity
@Table(name = "suivis_journaliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuiviJournalier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date du suivi
    private LocalDate date;

    // Qualité du sommeil : 1 (très mauvais) → 5 (très bon)
    private int qualiteSommeil;

    // Niveau de comportement : 1 (très agité) → 5 (très calme)
    private int niveauComportement;

    // Niveau de communication : 1 (aucune) → 5 (très bonne)
    private int niveauCommunication;

    // Nombre de crises dans la journée
    private int nombreCrises;

    // Notes libres du parent sur la journée
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * L'enfant concerné par ce suivi.
     */
    @ManyToOne
    @JoinColumn(name = "enfant_id")
    private Enfant enfant;

    /**
     * Le parent qui a enregistré ce suivi.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}
