package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Questionnaire;
import com.autiguide.autiguide.repository.QuestionnaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import com.autiguide.autiguide.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    public Questionnaire sauvegarder(Questionnaire questionnaire) {
        // Date de création automatique
        if (questionnaire.getDateCreation() == null) {
            questionnaire.setDateCreation(LocalDate.now());
        }
        return questionnaireRepository.save(questionnaire);
    }

    public Questionnaire trouverParId(Long id) {
        return questionnaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionnaire", id));
    }

    public List<Questionnaire> tousLesQuestionnaires() {
        return questionnaireRepository.findAll();
    }

    // Trouver questionnaires adaptés à l'âge de l'enfant
    public List<Questionnaire> trouverParAge(int age) {
        return questionnaireRepository.findByAgeEnfant(age);
    }

    public Questionnaire modifier(Long id, Questionnaire qModifie) {
        Questionnaire q = trouverParId(id);
        q.setTitre(qModifie.getTitre());
        q.setAgeMin(qModifie.getAgeMin());
        q.setAgeMax(qModifie.getAgeMax());
        return questionnaireRepository.save(q);
    }

    public void supprimer(Long id) {
        questionnaireRepository.deleteById(id);
    }
}