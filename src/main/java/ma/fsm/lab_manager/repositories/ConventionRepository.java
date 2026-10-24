package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Convention;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface ConventionRepository extends JpaRepository<Convention, Long> { }