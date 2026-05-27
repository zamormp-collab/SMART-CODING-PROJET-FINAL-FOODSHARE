/**
 * Enumération représentant le statut d'une offre alimentaire.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums;

/**
 * ACTIVE   : L'offre est disponible et visible par les étudiants
 * EXPIREE  : Le créneau de retrait est dépassé
 * ANNULEE  : L'offreur a annulé l'offre manuellement
 */
public enum StatutOffre {
    ACTIVE,
    EXPIREE,
    ANNULEE
}
