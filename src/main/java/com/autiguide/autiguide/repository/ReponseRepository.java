// ReponseRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des réponses.
 */
public interface ReponseRepository extends JpaRepository<Reponse, Long> {

    // Récupère toutes les réponses associées à un résultat donné
    List<Reponse> findByResultatId(Long resultatId);
}