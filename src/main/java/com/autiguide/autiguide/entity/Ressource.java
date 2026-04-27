package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Classe représentant une ressource éducative publiée par un admin.
 * Les parents peuvent consulter et rechercher ces ressources
 * par catégorie ou mot-clé.
 */
@Entity
@Table(name = "ressources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre de la ressource
    private String titre;

    // Contenu complet de l'article ou du conseil
    @Column(columnDefinition = "TEXT")
    private String contenu;

    // Catégorie (ex: "Communication", "Comportement", "Alimentation")
    private String categorie;

    // Mots-clés pour la recherche (ex: "autisme, langage, jeu")
    private String motsCles;

    // Date de publication de la ressource
    private LocalDate datePublication;

    /**
     * L'admin qui a publié cette ressource.
     * ManyToOne : un admin peut publier plusieurs ressources.
     */
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}