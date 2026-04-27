
// QuestionnaireRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository pour la gestion des questionnaires.
 */
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    /**
     * Récupère les questionnaires adaptés à l'âge de l'enfant.
     * Exemple : un enfant de 3 ans → questionnaires avec ageMin<=3 et ageMax>=3
     *
     * @param age âge de l'enfant en années
     * @return liste des questionnaires correspondants
     */
    @Query("SELECT q FROM Questionnaire q WHERE :age BETWEEN q.ageMin AND q.ageMax")
    List<Questionnaire> findByAgeEnfant(@Param("age") int age);
}