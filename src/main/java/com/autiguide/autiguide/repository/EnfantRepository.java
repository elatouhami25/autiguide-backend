// EnfantRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des enfants.
 */
public interface EnfantRepository extends JpaRepository<Enfant, Long> {

    // Récupère tous les enfants d'un parent spécifique
    List<Enfant> findByParentId(Long parentId);
}
