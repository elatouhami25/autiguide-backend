package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Admin;
import com.autiguide.autiguide.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.autiguide.autiguide.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin sauvegarder(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin trouverParId(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin introuvable id: " + id));
    }

    public Admin trouverParEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin introuvable email: " + email));
    }

    public List<Admin> tousLesAdmins() {
        return adminRepository.findAll();
    }

    public Admin modifier(Long id, Admin adminModifie) {
        Admin admin = trouverParId(id);
        admin.setNom(adminModifie.getNom());
        admin.setPrenom(adminModifie.getPrenom());
        admin.setEmail(adminModifie.getEmail());
        admin.setNiveauAcces(adminModifie.getNiveauAcces());
        return adminRepository.save(admin);
    }

    public void supprimer(Long id) {
        adminRepository.deleteById(id);
    }
}