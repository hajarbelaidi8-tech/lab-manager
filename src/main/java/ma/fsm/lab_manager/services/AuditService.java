package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.Historique;
import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.repositories.HistoriqueRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditService {
    @Autowired
    private HistoriqueRepository historiqueRepository;
    @Autowired
    private MembreRepository membreRepository;

    public void log(String action, String details) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        Membre membre = membreRepository.findByEmail(email);
        Historique historique = new Historique();
        historique.setAction(action);
        historique.setDetails(details);
        historique.setUtilisateur(membre);
        historique.setDateAction(LocalDateTime.now());
        // 2. Dir l-check 3la l-membre hna
        if (membre != null) {
            historique.setUtilisateur(membre);
        }
        historiqueRepository.save(historique);
    }
}