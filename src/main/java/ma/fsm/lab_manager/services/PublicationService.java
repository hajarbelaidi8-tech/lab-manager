package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.entities.Publication;
import ma.fsm.lab_manager.entities.RoleMembre;
import org.springframework.stereotype.Service;

@Service
public class PublicationService {

    /**
     * Vérifie si un utilisateur a le droit de modifier une publication.
     * Le Directeur peut tout modifier.
     * Un auteur peut modifier sa propre publication.
     */
    public boolean canUserEdit(Publication p, Membre user) {
        if (user == null) return false;
        
        // Le Directeur a tous les droits
        if (user.getRole() == RoleMembre.DIRECTEUR) {
            return true;
        }
        
        // Un auteur peut modifier sa propre publication
        if (p.getAuteurs() != null && p.getAuteurs().stream().anyMatch(a -> a.getId().equals(user.getId()))) {
            return true;
        }
        
        return false;
    }
}
