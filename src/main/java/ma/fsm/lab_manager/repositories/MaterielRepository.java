package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
@org.springframework.stereotype.Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> { }