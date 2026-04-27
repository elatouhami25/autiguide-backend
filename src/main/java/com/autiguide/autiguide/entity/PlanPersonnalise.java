package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe représentant le plan d'accompagnement personnalisé.
 * Généré automatiquement après le calcul du résultat.
 * Contient des conseils et objectifs adaptés au niveau de risque détecté.
 */
@Entity
@Table(name = "plans_personnalises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanPersonnalise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre du plan (ex: "Plan d'accompagnement - Niveau Moyen")
    private String titre;

    // Description détaillée du plan et des conseils
    @Column(columnDefinition = "TEXT")
    private String description;

    // Objectifs à atteindre pour le suivi de l'enfant
    @Column(columnDefinition = "TEXT")
    private String objectifs;

    // Date de génération automatique du plan
    private LocalDate dateGeneration;

    /**
     * Le résultat qui a déclenché la génération de ce plan.
     * OneToOne : un plan est lié à un seul résultat.
     */
    @OneToOne
    @JoinColumn(name = "resultat_id")
    private Resultat resultat;
}