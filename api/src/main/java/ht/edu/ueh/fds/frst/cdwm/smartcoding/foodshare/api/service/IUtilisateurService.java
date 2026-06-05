package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.UtilisateurUpdateRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.UtilisateurResponse;

import java.util.List;

public interface IUtilisateurService {

    /** Lister tous les utilisateurs */
    List<UtilisateurResponse> listerTous();

    /** Rechercher un utilisateur par son id */
    UtilisateurResponse trouverParId(Long id);

    /** Modifier un utilisateur */
    UtilisateurResponse modifier(Long id, UtilisateurUpdateRequest request);

    /** Supprimer un utilisateur */
    void supprimer(Long id);

    /** Rechercher par mot clé (nom, prénom, email) */
    List<UtilisateurResponse> rechercher(String motCle);
}