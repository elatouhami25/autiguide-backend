package com.autiguide.autiguide.service;

import com.autiguide.autiguide.entity.Enfant;
import com.autiguide.autiguide.repository.EnfantRepository;
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

    /**
     * Sauvegarde un nouvel enfant dans la base de données.
     * Appelé quand le parent ajoute un profil enfant.
     *
     * @param enfant L'objet enfant reçu du Controller
     * @return L'enfant sauvegardé avec son ID généré
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
    public Enfant update(Long id, Enfant newEnfant) {
        // On cherche l'enfant existant, sinon on lance une exception
        Enfant enfant = enfantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", id));
        // On met à jour uniquement les champs modifiables
        enfant.setPrenom(newEnfant.getPrenom());
        enfant.setDateNaissance(newEnfant.getDateNaissance());
        enfant.setSexe(newEnfant.getSexe());

        // On sauvegarde et retourne l'enfant modifié
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