package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe mère abstraite pour tous les utilisateurs de l'application.
 * Elle ne peut pas être instanciée directement.
 * Les classes Parent et Admin héritent de cette classe.
 *
 * Stratégie d'héritage : JOINED
 * → Chaque sous-classe a sa propre table en base de données
 * → La table "utilisateurs" contient les champs communs
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "utilisateurs")
@Data               // Lombok : génère automatiquement getters, setters, toString, equals
@NoArgsConstructor  // Lombok : génère un constructeur sans arguments
@AllArgsConstructor // Lombok : génère un constructeur avec tous les arguments
public abstract class Utilisateur {

    // Clé primaire auto-incrémentée
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de famille de l'utilisateur
    private String nom;

    // Prénom de l'utilisateur
    private String prenom;

    // Email unique - utilisé pour la connexion
    @Column(unique = true, nullable = false)
    private String email;

    // Mot de passe (sera chiffré avec BCrypt dans le service)
    @Column(nullable = false)
    private String motDePasse;

    // Date d'inscription automatiquement enregistrée
    private LocalDate dateInscription;
}