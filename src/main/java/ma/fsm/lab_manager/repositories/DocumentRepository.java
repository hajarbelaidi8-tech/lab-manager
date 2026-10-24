package ma.fsm.lab_manager.repositories;
import ma.fsm.lab_manager.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
@org.springframework.stereotype.Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    java.util.List<Document> findByTypeDocument(String typeDocument);
    Document findFirstByCheminFichier(String cheminFichier);
}