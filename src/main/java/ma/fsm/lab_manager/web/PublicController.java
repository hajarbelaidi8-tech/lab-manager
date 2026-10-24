package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.DemandeAdhesion;
import ma.fsm.lab_manager.repositories.DemandeAdhesionRepository;
import ma.fsm.lab_manager.repositories.EquipeRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.services.AuditService;
import ma.fsm.lab_manager.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Controller
public class PublicController {
    @Autowired private DemandeAdhesionRepository adhesionRepo;
    @Autowired private EquipeRepository equipeRepo;
    @Autowired private MembreRepository membreRepo;
    @Autowired private NotificationService notificationService;
    @Autowired private AuditService auditService;

    @Autowired private ma.fsm.lab_manager.repositories.PublicationRepository publicationRepo;
    @Autowired private ma.fsm.lab_manager.services.PublicationService publicationService;

    @GetMapping("/publication/{id}")
    public String publicationDetails(@PathVariable Long id, Model model, java.security.Principal principal) {
        ma.fsm.lab_manager.entities.Publication p = publicationRepo.findById(id).orElse(null);
        if (p == null) return "redirect:/";
        
        model.addAttribute("p", p);
        
        // VÉRIFICATION DE L'EXISTENCE PHYSIQUE DU FICHIER
        boolean fileExists = false;
        if (p.getCheminFichier() != null && !p.getCheminFichier().isEmpty()) {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads").resolve(p.getCheminFichier());
            fileExists = java.nio.file.Files.exists(filePath);
        }
        model.addAttribute("fileExists", fileExists);
        
        if (principal != null) {
            ma.fsm.lab_manager.entities.Membre user = membreRepo.findByEmail(principal.getName());
            model.addAttribute("canEdit", publicationService.canUserEdit(p, user));
        } else {
            model.addAttribute("canEdit", false);
        }

        //auditService.log("PUBLIC_VIEW_PUB", "ID: " + id + ", Titre: " + p.getTitre());
        return "public/publication_details";
    }

    @GetMapping("/postuler")
    public String form(Model model) {
        model.addAttribute("demande", new DemandeAdhesion());
        model.addAttribute("equipes", equipeRepo.findAll());
        return "public/form_adhesion";
    }

    @PostMapping("/postuler/save")
    public String save(@ModelAttribute DemandeAdhesion d, @RequestParam("cvFile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            try (java.io.InputStream is = file.getInputStream()) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            d.setCvPath(fileName);
        }
        adhesionRepo.save(d);

        // NOTIFICATION AU DIRECTEUR & AUDIT
        auditService.log("NEW_APPLICATION", "Candidat: " + d.getPrenom() + " " + d.getNom());
        notificationService.alertDirector("Nouvelle Candidature", 
            "Un nouveau chercheur (" + d.getPrenom() + " " + d.getNom() + ") souhaite rejoindre le laboratoire.");

        return "redirect:/postuler/confirmation";
    }

    @GetMapping("/postuler/confirmation")
    public String confirmation() {
        return "public/confirmation";
    }
}