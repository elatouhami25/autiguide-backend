// UtilisateurRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository de base pour les utilisateurs.
 * Utilisé principalement pour la vérification de l'email lors de l'inscription.
 * JpaRepository fournit automatiquement : save, findById, findAll, delete...
 */
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Recherche un utilisateur par son email (utilisé pour la connexion)
    Optional<Utilisateur> findByEmail(String email);

    // Vérifie si un email existe déjà (éviter les doublons à l'inscription)
    boolean existsByEmail(String email);
}
