package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@org.springframework.stereotype.Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {

    // Cette méthode est indispensable pour la page "Mon Profil" (Section 3 du PDF)
    Membre findByEmail(String email);
    java.util.List<Membre> findByRole(ma.fsm.lab_manager.entities.RoleMembre role);
    java.util.List<Membre> findByEquipeId(Long equipeId);

}