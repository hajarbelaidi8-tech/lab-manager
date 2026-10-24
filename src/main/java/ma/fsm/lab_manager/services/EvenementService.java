package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.Evenement;
import ma.fsm.lab_manager.repositories.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository evenementRepo;

    /**
     * Vérifie si l'utilisateur est l'un des organisateurs de l'événement.
     */
    public boolean isOrganizer(String email, Long evenementId) {
        if (evenementId == null) return true; // Cas de création d'un nouvel événement
        
        Evenement e = evenementRepo.findById(evenementId).orElse(null);
        if (e == null || e.getOrganisateurs() == null) return false;
        
        return e.getOrganisateurs().stream()
                .anyMatch(org -> org.getEmail().equals(email));
    }
}
