package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Admin;
import com.autiguide.autiguide.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    // ── POST /api/admin ──
    // Créer un admin
    @PostMapping
    public ResponseEntity<Admin> creer(@RequestBody Admin admin) {
        admin.setDateInscription(LocalDate.now());
        return ResponseEntity.ok(adminService.sauvegarder(admin));
    }

    // ── GET /api/admin ──
    // Lister tous les admins
    @GetMapping
    public ResponseEntity<List<Admin>> listerTous() {
        return ResponseEntity.ok(adminService.tousLesAdmins());
    }

    // ── GET /api/admin/{id} ──
    // Trouver un admin par id
    @GetMapping("/{id}")
    public ResponseEntity<Admin> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.trouverParId(id));
    }

    // ── PUT /api/admin/{id} ──
    // Modifier un admin
    @PutMapping("/{id}")
    public ResponseEntity<Admin> modifier(
            @PathVariable Long id,
            @RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.modifier(id, admin));
    }

    // ── DELETE /api/admin/{id} ──
    // Supprimer un admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        adminService.supprimer(id);
        return ResponseEntity.ok("Admin supprimé avec succès");
    }
}