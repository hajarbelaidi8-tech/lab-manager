package ma.fsm.lab_manager.web;


import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.services.MembreService;
import ma.fsm.lab_manager.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
public class MembreController {
    @Autowired private MembreService membreService;
    @Autowired private MembreRepository membreRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private NotificationService notificationService;

    @Autowired private ma.fsm.lab_manager.repositories.MandatRepository mandatRepo;

    @GetMapping("/membres")
    public String liste(Model model) {
        model.addAttribute("listeMembres", membreRepo.findAll());
        return "membres";
    }

    @GetMapping("/profil")
    public String profil(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        Membre m = membreRepo.findByEmail(principal.getName());
        model.addAttribute("membre", m);
        model.addAttribute("publications", membreService.getPublicationsByMembre(m));
        return "membres/profil";
    }

    @GetMapping("/membres/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        Membre m = membreRepo.findById(id).orElseThrow();
        model.addAttribute("membre", m);
        model.addAttribute("publications", membreService.getPublicationsByMembre(m));
        return "membres/profil";
    }

    @GetMapping("/membres/editer/{id}")
    @PreAuthorize("hasRole('VICE_DIRECTEUR') or @membreRepository.findById(#id).get().email == authentication.name")

    public String editer(@PathVariable Long id, Model model) {
        Membre m = membreRepo.findById(id).orElseThrow();
        model.addAttribute("membre", m);
        model.addAttribute("tousLesStatuts", StatutMembre.values());
        model.addAttribute("tousLesRoles", RoleMembre.values());
        return "membres/edit_membre";
    }

    @PostMapping("/membres/modifier") // URL changée en /admin
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTEUR') or hasRole('VICE_DIRECTEUR')")
    public String modifier(@ModelAttribute Membre membre,
                           @RequestParam("image") MultipartFile file,
                           @RequestParam(value = "nouveauPassword", required = false) String newPass) throws IOException { // ⭐ ZID HAD S-SṬER HNA ⭐
        Membre existing = membreRepo.findById(membre.getId()).orElseThrow();
        
        // HISTORISATION & PASSAGE DE RELAIS (Section 7 & 21 ⭐)
        if (membre.getRole() != null) {
            membreService.changerRole(existing.getId(), membre.getRole());
        }

        existing.setNom(membre.getNom());
        existing.setPrenom(membre.getPrenom());
        existing.setEtablissementOrigine(membre.getEtablissementOrigine());
        existing.setLaboratoireOrigine(membre.getLaboratoireOrigine());
        existing.setStatut(membre.getStatut());
        existing.setCentresInteret(membre.getCentresInteret());
        existing.setBiographie(membre.getBiographie());
        // ZID HAD L-SṬER HNA BACH D-DATE T-SAYV-A
        existing.setDateNaissance(membre.getDateNaissance());
        // ⭐ LOGIC D S-SAROUT (PASSWORD) ⭐
        if (newPass != null && !newPass.isEmpty()) {
            // Nta aslan 3ndek passwordEncoder m-injecté f l-fowq
            existing.setPassword(passwordEncoder.encode(newPass));
        }

        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            try (java.io.InputStream is = file.getInputStream()) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            existing.setPhoto(fileName);
        }

        
        membreRepo.save(existing);
        return "redirect:/membres";
    }

    @GetMapping("/admin/membres/supprimer/{id}") // URL changée en /admin
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(@PathVariable Long id) {
        membreService.supprimerMembre(id);
        return "redirect:/membres";
    }
    @GetMapping("/notifications/marquer-lue/{id}")
    public String lireNotification(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        notificationService.marquerLue(id);
        // On retourne à la page actuelle
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
    @GetMapping("/notifications/tout-lire")
    public String toutMarquerCommeLu(java.security.Principal principal, jakarta.servlet.http.HttpServletRequest request) {
        Membre m = membreRepo.findByEmail(principal.getName());
        // On récupère les notifs via le service
        List<Notification> notifs = notificationService.mesNotifications(m.getId());

        // On les marque toutes comme lues
        for(ma.fsm.lab_manager.entities.Notification n : notifs) {
            if(!n.isLue()) {
                notificationService.marquerLue(n.getId());
            }
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}