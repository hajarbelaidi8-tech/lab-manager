package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.entities.Notification;
import ma.fsm.lab_manager.entities.RoleMembre;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private MembreRepository membreRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private AuditService auditService;

    public void envoyerNotification(Membre destinataire, String titre, String message) {
        Notification notif = new Notification();
        notif.setTitre(titre);
        notif.setMessage(message);
        notif.setDestinataire(destinataire);
        notif.setDateCreation(LocalDateTime.now());
        notif.setLue(false);
        notificationRepository.save(notif);

        // Push real-time
        messagingTemplate.convertAndSendToUser(destinataire.getEmail(), "/queue/notifications", notif);
    }

    public void alertDirector(String sujet, String message) {
        List<Membre> directeurs = membreRepository.findAll().stream()
            .filter(m -> m.getRole() == RoleMembre.DIRECTEUR)
            .toList();

        for (Membre dir : directeurs) {
            envoyerNotification(dir, sujet, message);
        }
        auditService.log("DIRECTOR_ALERT", "Sujet: " + sujet);
    }

    public List<Notification> mesNotifications(Long membreId) {
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(membreId);
    }

    public void marquerLue(Long notifId) {
        Notification n = notificationRepository.findById(notifId).orElseThrow();
        n.setLue(true);
        notificationRepository.save(n);
    }
}