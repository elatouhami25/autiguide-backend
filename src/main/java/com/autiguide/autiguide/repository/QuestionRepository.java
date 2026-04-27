// QuestionRepository.java
package com.autiguide.autiguide.repository;

import com.autiguide.autiguide.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository pour la gestion des questions.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Récupère toutes les questions d'un questionnaire triées par ordre d'affichage
    List<Question> findByQuestionnaireIdOrderByOrdre(Long questionnaireId);
}
