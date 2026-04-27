// ParentRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour la gestion des parents.
 * Hérite de JpaRepository → toutes les opérations CRUD sont disponibles.
 */
public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Recherche un parent par email (connexion spécifique aux parents)
    Optional<Parent> findByEmail(String email);
}