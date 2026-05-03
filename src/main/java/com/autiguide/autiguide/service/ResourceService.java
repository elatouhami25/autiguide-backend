package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Ressource;
import com.autiguide.autiguide.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
class RessourceService {

    private final RessourceRepository ressourceRepository;

    public List<Ressource> getAll() {
        return ressourceRepository.findAll();
    }

    public List<Ressource> getByCategorie(String categorie) {
        return ressourceRepository.findByCategorie(categorie);
    }

    public List<Ressource> searchByMotCle(String motCle) {
        return ressourceRepository.findByMotsClesContainingIgnoreCase(motCle);
    }

    public Ressource save(Ressource r) {
        return ressourceRepository.save(r);
    }
}