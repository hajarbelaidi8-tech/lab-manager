package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Affectation;
import ma.fsm.lab_manager.entities.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findByMateriel(Materiel materiel);
}
