package com.autiguide.autiguide.exception;

/**
 * Exception levée quand une ressource n'est pas trouvée en base de données.
 * Exemple : Parent introuvable, Enfant introuvable...
 * Retourne automatiquement un code HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'id : " + id);
    }

    public ResourceNotFoundException(String ressource, String champ, String valeur) {
        super(ressource + " introuvable avec " + champ + " : " + valeur);
    }
}