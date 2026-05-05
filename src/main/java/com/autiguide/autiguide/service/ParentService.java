package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Enfant;
import com.autiguide.autiguide.entity.Parent;
import com.autiguide.autiguide.exception.ResourceNotFoundException;
import com.autiguide.autiguide.repository.EnfantRepository;
import com.autiguide.autiguide.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EnfantRepository enfantRepository;

    public Parent save(Parent parent) {
        return parentRepository.save(parent);
    }

    public Optional<Parent> findById(Long id) {
        return parentRepository.findById(id);
    }

    public List<Parent> findAll() {
        return parentRepository.findAll();
    }

    public void deleteById(Long id) {
        parentRepository.deleteById(id);
    }

    public Enfant ajouterEnfant(Long parentId, Enfant enfant) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));
        enfant.setParent(parent);
        return enfantRepository.save(enfant);
    }

    public List<Enfant> getEnfants(Long parentId) {
        return enfantRepository.findByParentId(parentId);
    }
}