// PlanPersonnaliseRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.PlanPersonnalise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour la gestion des plans personnalisés.
 */
public interface PlanPersonnaliseRepository extends JpaRepository<PlanPersonnalise, Long> {

    // Récupère le plan lié à un résultat spécifique
    Optional<PlanPersonnalise> findByResultatId(Long resultatId);
}
