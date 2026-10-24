package ma.fsm.lab_manager.config;

import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.entities.StatutMembre;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MembreRepository membreRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Membre membre = membreRepository.findByEmail(email);
        if (membre == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email);
        }

        // Section 2.2.D & E du PDF : Blocage des Retraités et Anciens
        if (membre.getStatut() == StatutMembre.RETRAITE || membre.getStatut() == StatutMembre.ANCIEN) {
            throw new DisabledException("Votre compte est désactivé (Historique conservé uniquement)");
        }

        return User.builder()
                .username(membre.getEmail())
                .password(membre.getPassword())
                .roles(membre.getRole().name())
                .build();
    }
}
