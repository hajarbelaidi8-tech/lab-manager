package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdAndLueFalse(Long membreId);
    List<Notification> findByDestinataireIdOrderByDateCreationDesc(Long membreId);
}