// AdminRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour la gestion des administrateurs.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Recherche un admin par email
    Optional<Admin> findByEmail(String email);
}