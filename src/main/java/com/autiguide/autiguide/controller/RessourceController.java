package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Ressource;
import com.autiguide.autiguide.repository.RessourceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ressources")
public class RessourceController {

    private final RessourceRepository ressourceRepo;

    public RessourceController(RessourceRepository ressourceRepo) {
        this.ressourceRepo = ressourceRepo;
    }

    @GetMapping
    public List<Ressource> getAll() {
        return ressourceRepo.findAll();
    }
}