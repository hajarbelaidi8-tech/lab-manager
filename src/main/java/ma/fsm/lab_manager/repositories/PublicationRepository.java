package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ma.fsm.lab_manager.entities.Membre;

@org.springframework.stereotype.Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByAuteurs(Membre m);
}