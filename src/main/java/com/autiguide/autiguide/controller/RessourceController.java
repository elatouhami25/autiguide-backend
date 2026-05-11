package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Ressource;
import com.autiguide.autiguide.repository.RessourceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@CrossOrigin(origins = "*")
public class RessourceController {

    private final RessourceRepository ressourceRepo;

    public RessourceController(RessourceRepository ressourceRepo) {
        this.ressourceRepo = ressourceRepo;
    }

    // GET /api/ressources — lister toutes les ressources
    @GetMapping
    public List<Ressource> getAll() {
        return ressourceRepo.findAll();
    }

    // GET /api/ressources/{id} — trouver par id
    @GetMapping("/{id}")
    public ResponseEntity<Ressource> getById(@PathVariable Long id) {
        return ressourceRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/ressources — créer une ressource
    @PostMapping
    public ResponseEntity<Ressource> create(@RequestBody Ressource ressource) {
        ressource.setDatePublication(LocalDate.now());
        return ResponseEntity.ok(ressourceRepo.save(ressource));
    }

    // PUT /api/ressources/{id} — modifier une ressource
    @PutMapping("/{id}")
    public ResponseEntity<Ressource> update(
            @PathVariable Long id,
            @RequestBody Ressource updated) {
        return ressourceRepo.findById(id).map(existing -> {
            existing.setTitre(updated.getTitre());
            existing.setContenu(updated.getContenu());
            existing.setCategorie(updated.getCategorie());
            existing.setMotsCles(updated.getMotsCles());
            return ResponseEntity.ok(ressourceRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/ressources/{id} — supprimer une ressource
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ressourceRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ressourceRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // GET /api/ressources/categorie/{cat} — filtrer par catégorie
    @GetMapping("/categorie/{categorie}")
    public List<Ressource> getByCategorie(@PathVariable String categorie) {
        return ressourceRepo.findByCategorie(categorie);
    }
}
