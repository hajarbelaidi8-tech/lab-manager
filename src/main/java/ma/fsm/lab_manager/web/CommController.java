package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Message;
import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.repositories.MessageRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.repositories.EquipeRepository;
import ma.fsm.lab_manager.repositories.EvenementRepository;
import ma.fsm.lab_manager.services.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/communication")
public class CommController {

    @Autowired private MessageRepository messageRepo;
    @Autowired private MembreRepository membreRepo;
    @Autowired private EquipeRepository equipeRepo;

    @Autowired private CommunicationService communicationService;
    @Autowired private EvenementRepository evenementRepo;

    @GetMapping("/mur")
    public String mur(Model model, Principal principal, 
                      @RequestParam(required = false) Long eventId,
                      @RequestParam(required = false) Long equipeId) {
        if (principal == null) return "redirect:/login";
        Membre currentUser = membreRepo.findByEmail(principal.getName());
        model.addAttribute("currentUser", currentUser);
        
        java.util.List<Message> messages;
        if (eventId != null) {
            messages = communicationService.getEventMessages(eventId);
            model.addAttribute("currentEvent", evenementRepo.findById(eventId).orElse(null));
        } else if (equipeId != null) {
            messages = communicationService.getTeamMessages(equipeId);
            model.addAttribute("currentEquipe", equipeRepo.findById(equipeId).orElse(null));
        } else {
            messages = communicationService.getPublicMessages();
        }
        
        model.addAttribute("messages", messages);
        model.addAttribute("equipes", equipeRepo.findAll());
        model.addAttribute("evenements", evenementRepo.findAll());
        return "communication/mur";
    }

    @PostMapping("/poster")
    public String poster(@RequestParam String contenu, 
                         @RequestParam(required = false) Long equipeId, 
                         @RequestParam(required = false) Long eventId,
                         Principal principal) {
        if (principal != null && !contenu.isBlank()) {
            Membre user = membreRepo.findByEmail(principal.getName());
            Message msg = new Message();
            msg.setContenu(contenu);
            msg.setExpediteur(user);
            if (equipeId != null) msg.setEquipe(equipeRepo.findById(equipeId).orElse(null));
            if (eventId != null) msg.setEvenement(evenementRepo.findById(eventId).orElse(null));
            communicationService.saveMessage(msg);
        }
        return "redirect:/communication/mur" + (eventId != null ? "?eventId=" + eventId : "");
    }
}
