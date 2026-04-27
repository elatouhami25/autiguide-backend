package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Classe représentant un parent utilisateur de l'application.
 * Hérite de Utilisateur (récupère id, nom, prenom, email, motDePasse, dateInscription).
 *
 * Un parent peut :
 * - Avoir plusieurs enfants
 * - Enregistrer des suivis journaliers
 * - Remplir des questionnaires
 */
@Entity
@Table(name = "parents")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Indique que la clé primaire de cette table est une clé étrangère vers "utilisateurs"
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Parent extends Utilisateur {

    // Numéro de téléphone du parent (optionnel)
    private String telephone;

    /**
     * Liste des enfants associés à ce parent.
     * cascade = ALL : si on supprime le parent, ses enfants sont supprimés aussi
     * fetch = LAZY : les enfants ne sont chargés que quand on en a besoin (performance)
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enfant> enfants;

    /**
     * Liste des suivis journaliers enregistrés par ce parent.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SuiviJournalier> suivis;
}
