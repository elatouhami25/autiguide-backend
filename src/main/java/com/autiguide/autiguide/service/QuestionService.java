package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Question;
import com.autiguide.autiguide.entity.Questionnaire;
import com.autiguide.autiguide.repository.QuestionRepository;
import com.autiguide.autiguide.repository.QuestionnaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.autiguide.autiguide.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionnaireRepository questionnaireRepository;

    public Question sauvegarder(Long questionnaireId, Question question) {
        // Lier la question au questionnaire
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionnaire", questionnaireId));
        question.setQuestionnaire(questionnaire);
        return questionRepository.save(question);
    }

    public Question trouverParId(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }

    // Récupérer toutes les questions d'un questionnaire triées par ordre
    public List<Question> trouverParQuestionnaire(Long questionnaireId) {
        return questionRepository.findByQuestionnaireIdOrderByOrdre(questionnaireId);
    }

    public Question modifier(Long id, Question qModifiee) {
        Question q = trouverParId(id);
        q.setContenu(qModifiee.getContenu());
        q.setOrdre(qModifiee.getOrdre());
        return questionRepository.save(q);
    }

    public void supprimer(Long id) {
        questionRepository.deleteById(id);
    }
}