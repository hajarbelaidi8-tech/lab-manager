package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Mandat;
import org.springframework.data.jpa.repository.JpaRepository;
@org.springframework.stereotype.Repository
public interface MandatRepository extends JpaRepository<Mandat, Long> {
    java.util.List<ma.fsm.lab_manager.entities.Mandat> findByMembreAndTypeRoleAndDateFinIsNull(ma.fsm.lab_manager.entities.Membre membre, ma.fsm.lab_manager.entities.RoleMembre typeRole);
}