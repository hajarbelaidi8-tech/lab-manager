package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Evenement;
import ma.fsm.lab_manager.repositories.EvenementRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.services.NotificationService;
import ma.fsm.lab_manager.services.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired private EvenementRepository evenementRepo;
    @Autowired private MembreRepository membreRepo;
    @Autowired private ma.fsm.lab_manager.services.AuditService auditService;

    @GetMapping
    public String liste(@RequestParam(required = false) Integer annee, Model model) {
        if (annee != null) {
            model.addAttribute("evenements", evenementRepo.findAll().stream()
                    .filter(e -> e.getDateEvenement() != null && e.getDateEvenement().getYear() == annee)
                    .collect(java.util.stream.Collectors.toList()));
        } else {
            model.addAttribute("evenements", evenementRepo.findAll());
        }
        return "evenements/liste_evenements";
    }

    @GetMapping("/nouveau")
    public String form(Model model) {
        model.addAttribute("evenement", new Evenement());
        model.addAttribute("tousLesMembres", membreRepo.findAll());
        return "evenements/form_evenement";
    }

    @Autowired private NotificationService notificationService;
    @Autowired private EvenementService evenementService;

    @PostMapping("/save")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('DIRECTEUR') or ( #e.id != null and @evenementService.isOrganizer(authentication.name, #e.id) ) or ( #e.id == null )")
    public String save(Evenement e) {
        evenementRepo.save(e);
        
        // NOTIFICATION GÉNÉRALE
        String msg = "Nouvel événement : " + e.getTitre();
        for (ma.fsm.lab_manager.entities.Membre m : membreRepo.findAll()) {
            notificationService.envoyerNotification(m, "Nouvel Événement", msg);
        }
        
        return "redirect:/evenements";
    }

    @GetMapping("/editer/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('DIRECTEUR') or @evenementService.isOrganizer(authentication.name, #id)")
    public String editer(@PathVariable Long id, Model model) {
        Evenement e = evenementRepo.findById(id).orElseThrow();
        model.addAttribute("evenement", e);
        model.addAttribute("tousLesMembres", membreRepo.findAll());
        return "evenements/form_evenement";
    }

    //public String supprimer(@PathVariable Long id) {
        // ⭐ ZID HADI BACH T- Match-I L-HISTORIQUE (Section 21)
        //Evenement e = evenementRepo.findById(id).orElse(null);
       // if (e != null) {
           // auditService.log("EVENT_DELETED", "L'événement '" + e.getTitre() + "' a été supprimé.");
           // evenementRepo.deleteById(id);
        //}
        //return "redirect:/evenements";
    //}

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Evenement e = evenementRepo.findById(id).orElse(null);
        if (e == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Événement introuvable (ID: " + id + ")");
            return "redirect:/evenements";
        }
        model.addAttribute("evenement", e);
        return "evenements/details_evenement";
    }
    @GetMapping("/supprimer/{id}") // ⭐ Nssiti hadi !
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DIRECTEUR', 'ADMIN')")
    public String supprimer(@PathVariable Long id) {
        Evenement e = evenementRepo.findById(id).orElse(null);
        if (e != null) {
            auditService.log("EVENT_DELETED", "L'événement '" + e.getTitre() + "' a été supprimé.");
            evenementRepo.deleteById(id);
        }
        return "redirect:/evenements";
    }

    @GetMapping("/calendrier")
    public String calendrier(Model model) {
        model.addAttribute("evenements", evenementRepo.findAll().stream()
                .sorted(java.util.Comparator.comparing(Evenement::getDateEvenement, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .collect(java.util.stream.Collectors.toList()));
        return "evenements/calendrier";
    }
}