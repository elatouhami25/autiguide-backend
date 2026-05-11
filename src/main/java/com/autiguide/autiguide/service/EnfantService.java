package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Enfant;
import com.autiguide.autiguide.entity.Parent;
import com.autiguide.autiguide.repository.EnfantRepository;
import com.autiguide.autiguide.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.autiguide.autiguide.exception.ResourceNotFoundException;
/**
 * Service gérant la logique métier liée aux enfants.
 * C'est ici qu'on traite les données avant de les envoyer à la base de données.
 * Le Controller appelle le Service, qui appelle le Repository.
 */
@Service // Indique à Spring que c'est un service (composant métier)
public class EnfantService {

    /**
     * Injection automatique du repository.
     * Spring crée l'instance et l'injecte ici automatiquement.
     */
    @Autowired
    private EnfantRepository enfantRepository;

    @Autowired
    private ParentRepository parentRepository;

    /**
     * Sauvegarde un enfant en le liant à son parent via parentId.
     * Accepte un Map avec : prenom, dateNaissance, sexe, parentId
     */
    public Enfant saveWithParent(java.util.Map<String, Object> body) {
        Enfant enfant = new Enfant();
        enfant.setPrenom((String) body.get("prenom"));
        enfant.setSexe((String) body.get("sexe"));

        // Conversion de la date
        String dateStr = (String) body.get("dateNaissance");
        if (dateStr != null) {
            enfant.setDateNaissance(java.time.LocalDate.parse(dateStr));
        }

        // Lier au parent si parentId fourni
        Object parentIdObj = body.get("parentId");
        if (parentIdObj != null) {
            Long parentId = Long.valueOf(parentIdObj.toString());
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));
            enfant.setParent(parent);
        }

        return enfantRepository.save(enfant);
    }

    /**
     * Sauvegarde un nouvel enfant dans la base de données.
     */
    public Enfant save(Enfant enfant) {
        return enfantRepository.save(enfant);
    }

    /**
     * Recherche un enfant par son ID.
     * Retourne Optional pour éviter NullPointerException si non trouvé.
     *
     * @param id L'identifiant de l'enfant
     * @return Optional contenant l'enfant ou vide si non trouvé
     */
    public Optional<Enfant> findById(Long id) {
        return enfantRepository.findById(id);
    }

    /**
     * Récupère tous les enfants (pour l'admin).
     *
     * @return Liste de tous les enfants
     */
    public List<Enfant> findAll() {
        return enfantRepository.findAll();
    }

    /**
     * Récupère tous les enfants associés à un parent spécifique.
     * Utilisé pour afficher la liste des enfants d'un parent connecté.
     *
     * @param parentId L'identifiant du parent
     * @return Liste des enfants du parent
     */
    public List<Enfant> findByParentId(Long parentId) {
        return enfantRepository.findByParentId(parentId);
    }

    /**
     * Met à jour les informations d'un enfant existant.
     * Vérifie d'abord que l'enfant existe avant de modifier.
     *
     * @param id L'identifiant de l'enfant à modifier
     * @param newEnfant Les nouvelles données
     * @return L'enfant mis à jour
     */
    public Enfant update(Long id, java.util.Map<String, Object> body) {
        Enfant enfant = enfantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", id));

        if (body.get("prenom") != null) enfant.setPrenom((String) body.get("prenom"));
        if (body.get("sexe") != null) enfant.setSexe((String) body.get("sexe"));
        if (body.get("dateNaissance") != null) {
            enfant.setDateNaissance(java.time.LocalDate.parse((String) body.get("dateNaissance")));
        }
        // Mettre à jour le parent si fourni
        Object parentIdObj = body.get("parentId");
        if (parentIdObj != null) {
            Long parentId = Long.valueOf(parentIdObj.toString());
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));
            enfant.setParent(parent);
        }
        return enfantRepository.save(enfant);
    }

    /**
     * Supprime un enfant par son ID.
     * La suppression est en cascade (voir entity Enfant).
     *
     * @param id L'identifiant de l'enfant à supprimer
     */
    public void deleteById(Long id) {
        enfantRepository.deleteById(id);
    }
}