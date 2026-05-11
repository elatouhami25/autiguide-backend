package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Admin;
import com.autiguide.autiguide.entity.Parent;
import com.autiguide.autiguide.repository.AdminRepository;
import com.autiguide.autiguide.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final ParentRepository parentRepository;
    private final AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String motDePasse = body.get("motDePasse");

        // Vérifier Admin
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent() &&
                admin.get().getMotDePasse().equals(motDePasse)) {
            return ResponseEntity.ok(Map.of(
                    "id", admin.get().getId(),
                    "nom", admin.get().getNom(),
                    "prenom", admin.get().getPrenom(),
                    "email", admin.get().getEmail(),
                    "role", "ADMIN"
            ));
        }

        // Vérifier Parent
        Optional<Parent> parent = parentRepository.findByEmail(email);
        if (parent.isPresent() &&
                parent.get().getMotDePasse().equals(motDePasse)) {
            return ResponseEntity.ok(Map.of(
                    "id", parent.get().getId(),
                    "nom", parent.get().getNom(),
                    "prenom", parent.get().getPrenom(),
                    "email", parent.get().getEmail(),
                    "role", "PARENT"
            ));
        }

        return ResponseEntity.status(401).body(
                Map.of("message", "Email ou mot de passe incorrect")
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Parent parent) {
        if (parentRepository.findByEmail(parent.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email déjà utilisé")
            );
        }
        parent.setDateInscription(LocalDate.now());
        Parent saved = parentRepository.save(parent);
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "nom", saved.getNom(),
                "prenom", saved.getPrenom(),
                "email", saved.getEmail(),
                "role", "PARENT"
        ));
    }
}