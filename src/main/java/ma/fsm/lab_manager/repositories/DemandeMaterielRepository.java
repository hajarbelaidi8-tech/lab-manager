package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.DemandeMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface DemandeMaterielRepository extends JpaRepository<DemandeMateriel, Long> { }