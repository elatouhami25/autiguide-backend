
package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe représentant un administrateur de l'application.
 * Hérite de Utilisateur (récupère id, nom, prenom, email, motDePasse, dateInscription).
 *
 * Un admin peut :
 * - Gérer les utilisateurs
 * - Publier des ressources éducatives
 * - Administrer les questionnaires
 */
@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@EqualsAndHashCode(callSuper = false)
public class Admin extends Utilisateur {

    /**
     * Niveau d'accès de l'administrateur.
     * Exemple : 1 = admin normal, 2 = super admin
     */
    private int niveauAcces;
}