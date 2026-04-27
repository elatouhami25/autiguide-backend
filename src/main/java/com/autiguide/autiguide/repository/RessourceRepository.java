// RessourceRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des ressources éducatives.
 */
public interface RessourceRepository extends JpaRepository<Ressource, Long> {

    // Filtre les ressources par catégorie (ex: "Communication")
    List<Ressource> findByCategorie(String categorie);

    // Recherche dans les mots-clés (insensible à la casse)
    List<Ressource> findByMotsClesContainingIgnoreCase(String motCle);

    // Recherche dans le titre (insensible à la casse)
    List<Ressource> findByTitreContainingIgnoreCase(String titre);
}
