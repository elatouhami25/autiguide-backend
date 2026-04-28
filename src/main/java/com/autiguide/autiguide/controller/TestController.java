package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.*;
import com.autiguide.autiguide.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final ParentRepository parentRepository;
    private final EnfantRepository enfantRepository;
    private final AdminRepository adminRepository;

    // ── POST /api/test/parent ──
    // Crée un parent de test dans la base de données
    @PostMapping("/parent")
    public ResponseEntity<Parent> creerParent() {
        Parent parent = new Parent();
        parent.setNom("Ben Ali");
        parent.setPrenom("Fatima");
        parent.setEmail("fatima@test.com");
        parent.setMotDePasse("1234");
        parent.setTelephone("55123456");
        parent.setDateInscription(LocalDate.now());
        return ResponseEntity.ok(parentRepository.save(parent));
    }

    // ── POST /api/test/enfant/{parentId} ──
    // Crée un enfant lié au parent avec l'id donné
    @PostMapping("/enfant/{parentId}")
    public ResponseEntity<Enfant> creerEnfant(@PathVariable Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent introuvable"));
        Enfant enfant = new Enfant();
        enfant.setPrenom("Ahmed");
        enfant.setDateNaissance(LocalDate.of(2021, 3, 15));
        enfant.setSexe("M");
        enfant.setParent(parent);
        return ResponseEntity.ok(enfantRepository.save(enfant));
    }

    // ── POST /api/test/admin ──
    // Crée un admin de test
    @PostMapping("/admin")
    public ResponseEntity<Admin> creerAdmin() {
        Admin admin = new Admin();
        admin.setNom("Touhami");
        admin.setPrenom("Ela");
        admin.setEmail("admin@autiguide.com");
        admin.setMotDePasse("admin123");
        admin.setNiveauAcces(1);
        admin.setDateInscription(LocalDate.now());
        return ResponseEntity.ok(adminRepository.save(admin));
    }

    // ── GET /api/test/parents ──
    // Liste tous les parents
    @GetMapping("/parents")
    public ResponseEntity<?> listerParents() {
        return ResponseEntity.ok(parentRepository.findAll());
    }

    // ── GET /api/test/enfants ──
    // Liste tous les enfants
    @GetMapping("/enfants")
    public ResponseEntity<?> listerEnfants() {
        return ResponseEntity.ok(enfantRepository.findAll());
    }
}