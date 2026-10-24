package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired private MembreRepository membreRepo;
    @Autowired private ma.fsm.lab_manager.services.NotificationService notificationService;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (principal != null) {
            Membre currentMember = membreRepo.findByEmail(principal.getName());
            model.addAttribute("currentUser", currentMember);
            
            // NOTIFICATIONS RÉELLES (Section 19 PDF)
            if (currentMember != null) {
                java.util.List<ma.fsm.lab_manager.entities.Notification> allNotifs = notificationService.mesNotifications(currentMember.getId());
                long unreadCount = allNotifs.stream().filter(n -> !n.isLue()).count();
                model.addAttribute("notifications", allNotifs.stream().limit(10).toList());
                model.addAttribute("nbNotifs", unreadCount);
            }
        }
    }
}