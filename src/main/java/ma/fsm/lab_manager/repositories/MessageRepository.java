package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@org.springframework.stereotype.Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEquipeIdOrderByDateEnvoiAsc(Long equipeId);
    List<Message> findByEvenementIdOrderByDateEnvoiAsc(Long evenementId);
    List<Message> findByEquipeIsNullAndEvenementIsNullOrderByDateEnvoiAsc();
    List<Message> findAllByOrderByDateEnvoiDesc();
}