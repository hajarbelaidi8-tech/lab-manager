package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> { }