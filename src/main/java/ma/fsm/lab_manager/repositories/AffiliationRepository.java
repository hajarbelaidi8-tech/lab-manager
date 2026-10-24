package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Affiliation;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface AffiliationRepository extends JpaRepository<Affiliation, Long> { }