package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Laboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@org.springframework.stereotype.Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> { }
