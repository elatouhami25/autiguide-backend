package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Enfant;
import com.autiguide.autiguide.entity.Parent;
import com.autiguide.autiguide.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parents")
@CrossOrigin(origins = "*")
public class ParentController {

    @Autowired
    private ParentService parentService;

    // Ajouter un parent
    @PostMapping
    public ResponseEntity<Parent> save(@RequestBody Parent parent) {
        return ResponseEntity.ok(parentService.save(parent));
    }

    // Trouver par ID
    @GetMapping("/{id}")
    public ResponseEntity<Parent> findById(@PathVariable Long id) {
        return parentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tous les parents
    @GetMapping
    public ResponseEntity<List<Parent>> findAll() {
        return ResponseEntity.ok(parentService.findAll());
    }

    // Supprimer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Ajouter enfant à un parent
    @PostMapping("/{id}/enfants")
    public ResponseEntity<Enfant> ajouterEnfant(
            @PathVariable Long id,
            @RequestBody Enfant enfant) {
        return ResponseEntity.ok(parentService.ajouterEnfant(id, enfant));
    }

    // Liste enfants d'un parent
    @GetMapping("/{id}/enfants")
    public ResponseEntity<List<Enfant>> getEnfants(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getEnfants(id));
    }
}