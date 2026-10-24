package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
@org.springframework.stereotype.Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> { }