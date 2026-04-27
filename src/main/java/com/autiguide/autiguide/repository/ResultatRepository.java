// ResultatRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Resultat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des résultats.
 */
public interface ResultatRepository extends JpaRepository<Resultat, Long> {

    // Récupère tous les résultats d'un enfant
    List<Resultat> findByEnfantId(Long enfantId);

    // Récupère les résultats d'un enfant triés du plus récent au plus ancien
    List<Resultat> findByEnfantIdOrderByDateEvaluationDesc(Long enfantId);
}
