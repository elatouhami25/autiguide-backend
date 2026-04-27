// SuiviJournalierRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.SuiviJournalier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion des suivis journaliers.
 */
public interface SuiviJournalierRepository extends JpaRepository<SuiviJournalier, Long> {

    // Récupère tous les suivis d'un enfant
    List<SuiviJournalier> findByEnfantId(Long enfantId);

    // Récupère tous les suivis enregistrés par un parent
    List<SuiviJournalier> findByParentId(Long parentId);

    // Récupère les suivis d'un enfant entre deux dates (pour l'historique)
    List<SuiviJournalier> findByEnfantIdAndDateBetween(
            Long enfantId, LocalDate debut, LocalDate fin
    );
}
