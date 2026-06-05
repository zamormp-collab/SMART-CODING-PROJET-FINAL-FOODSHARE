/**
 * Enumération représentant l'état d'une réservation.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums;

/**
 * EN_ATTENTE   : la portion est réservée mais pas encore retirée
 * RETIREE      : l'étudiant a récupéré sa portion
 * NON_RETIREE  : l'étudiant n'est pas venu récupérer sa portion
 */
public enum StatutReservation {
    EN_ATTENTE,
    RETIREE,
    NON_RETIREE
}
