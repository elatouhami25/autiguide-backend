package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    List<Ressource> findByCategorie(String categorie);
    List<Ressource> findByMotsClesContainingIgnoreCase(String motCle);
    List<Ressource> findByTitreContainingIgnoreCase(String titre);
}