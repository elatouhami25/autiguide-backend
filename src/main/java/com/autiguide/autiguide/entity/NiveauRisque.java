package com.autiguide.autiguide.entity;

/**
 * Enumération représentant le niveau de risque TSA détecté.
 *
 * FAIBLE  → score entre 0 et 3  : peu de signes détectés
 * MOYEN   → score entre 4 et 7  : quelques signes détectés
 * ELEVE   → score supérieur à 7 : nombreux signes détectés
 *
 * ⚠️ Ce résultat ne remplace pas un diagnostic médical officiel.
 */
public enum NiveauRisque {
    FAIBLE,
    MOYEN,
    ELEVE
}
