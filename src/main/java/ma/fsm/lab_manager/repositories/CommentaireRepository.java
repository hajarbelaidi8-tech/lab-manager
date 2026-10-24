package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@org.springframework.stereotype.Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByPublicationId(Long publicationId);
    List<Commentaire> findByDocumentId(Long documentId);
}
