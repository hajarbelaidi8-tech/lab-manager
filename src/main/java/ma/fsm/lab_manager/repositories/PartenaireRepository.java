package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface PartenaireRepository extends JpaRepository<Partenaire, Long> { }