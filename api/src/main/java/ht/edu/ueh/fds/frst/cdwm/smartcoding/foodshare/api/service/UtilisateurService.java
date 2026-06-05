package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.UtilisateurUpdateRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.UtilisateurResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.RessourceIntrouvableException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService implements IUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerTous() {
        return utilisateurRepository.findAll()
                .stream()
                .map(UtilisateurResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse trouverParId(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Utilisateur introuvable : id=" + id));
        return UtilisateurResponse.from(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse modifier(Long id, UtilisateurUpdateRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Utilisateur introuvable : id=" + id));

        // Mise à jour uniquement des champs fournis (non null)
        if (request.getNom() != null) {
            utilisateur.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            utilisateur.setPrenom(request.getPrenom());
        }
        if (request.getAdresse() != null) {
            utilisateur.setAdresse(request.getAdresse());
        }
        if (request.getTelephone() != null) {
            utilisateur.setTelephone(request.getTelephone());
        }

        return UtilisateurResponse.from(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Utilisateur introuvable : id=" + id));
        utilisateurRepository.delete(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> rechercher(String motCle) {
        return utilisateurRepository.rechercherParMotCle(motCle)
                .stream()
                .map(UtilisateurResponse::from)
                .toList();
    }
}