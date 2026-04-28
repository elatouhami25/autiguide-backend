package com.autiguide.autiguide.controller;

import com.autiguide.autiguide.entity.Enfant;
import com.autiguide.autiguide.service.EnfantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller REST pour la gestion des enfants.
 * Reçoit les requêtes HTTP du Frontend et les transmet au Service.
 *
 * Routes disponibles:
 * POST   /api/enfants              → Ajouter un enfant
 * GET    /api/enfants/{id}         → Trouver un enfant par ID
 * GET    /api/enfants/parent/{id}  → Enfants d'un parent
 * PUT    /api/enfants/{id}         → Modifier un enfant
 * DELETE /api/enfants/{id}         → Supprimer un enfant
 */
@RestController // Indique que c'est un controller REST (retourne du JSON)
@RequestMapping("/api/enfants") // Préfixe de toutes les routes
@CrossOrigin(origins = "*") // Autorise les requêtes depuis le Frontend (React/Angular)
public class EnfantController {

    /**
     * Injection du service.
     * Le Controller ne parle qu'au Service, jamais directement au Repository.
     */
    @Autowired
    private EnfantService enfantService;

    /**
     * Ajoute un nouvel enfant.
     * Le parent doit être déjà connecté et son ID inclus dans l'objet enfant.
     *
     * @param enfant Données de l'enfant reçues en JSON
     * @return L'enfant créé avec son ID
     */
    @PostMapping
    public ResponseEntity<Enfant> save(@RequestBody Enfant enfant) {
        return ResponseEntity.ok(enfantService.save(enfant));
    }

    /**
     * Récupère un enfant par son ID.
     * Retourne 404 si l'enfant n'existe pas.
     *
     * @param id L'identifiant de l'enfant
     * @return L'enfant trouvé ou 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Enfant> findById(@PathVariable Long id) {
        return enfantService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère tous les enfants d'un parent.
     * Utilisé dans le dashboard du parent pour lister ses enfants.
     *
     * @param parentId L'identifiant du parent
     * @return Liste des enfants
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Enfant>> findByParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(enfantService.findByParentId(parentId));
    }

    /**
     * Modifie les informations d'un enfant existant.
     * Le parent peut modifier le prénom, date de naissance ou sexe.
     *
     * @param id L'identifiant de l'enfant à modifier
     * @param enfant Les nouvelles données en JSON
     * @return L'enfant modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<Enfant> update(
            @PathVariable Long id,
            @RequestBody Enfant enfant) {
        return ResponseEntity.ok(enfantService.update(id, enfant));
    }

    /**
     * Supprime un enfant par son ID.
     * Attention: supprime aussi les suivis et résultats liés (cascade).
     *
     * @param id L'identifiant de l'enfant à supprimer
     * @return 200 OK si supprimé avec succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enfantService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}