package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface ReunionRepository extends JpaRepository<Reunion, Long> { }