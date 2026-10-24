package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Convention;
import ma.fsm.lab_manager.entities.Partenaire;
import ma.fsm.lab_manager.entities.TypeDocument;
import ma.fsm.lab_manager.services.GouvernanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/gouvernance")
public class ConventionController {
    @Autowired private GouvernanceService gouvernanceService;

    @Autowired private ma.fsm.lab_manager.repositories.DocumentRepository documentRepo;

    @GetMapping("/conventions")
    public String liste(Model model) {
        model.addAttribute("conventions", gouvernanceService.listerConventions());
        model.addAttribute("partenaires", gouvernanceService.listerPartenaires());
        return "ressources/conventions";
    }

    @PostMapping("/partenaires/ajouter")
    public String ajouterPartenaire(Partenaire p) {
        gouvernanceService.ajouterPartenaire(p);
        return "redirect:/gouvernance/conventions";
    }

    @Autowired private ma.fsm.lab_manager.repositories.MembreRepository membreRepo;
    @Autowired private ma.fsm.lab_manager.services.NotificationService notificationService;

    @PostMapping("/conventions/creer")
    public String creerConvention(@ModelAttribute ma.fsm.lab_manager.entities.Convention c, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);
            try (java.io.InputStream is = file.getInputStream()) {
                java.nio.file.Files.copy(is, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            
            ma.fsm.lab_manager.entities.Document doc = new ma.fsm.lab_manager.entities.Document();
            doc.setTitre("Contrat: " + c.getTitre());
            doc.setCheminFichier(fileName);
            //doc.setTypeDocument("CONVENTION");
            doc.setTypeDocument(TypeDocument.ADMINISTRATIF);
            documentRepo.save(doc);
            c.setDocument(doc);
            
            // NOTIFICATION
            String msg = "Nouveau document (Convention) : " + c.getTitre();
            for (ma.fsm.lab_manager.entities.Membre m : membreRepo.findAll()) {
                notificationService.envoyerNotification(m, "Nouveau Document", msg);
            }
        }
        gouvernanceService.creerConvention(c);
        return "redirect:/gouvernance/conventions";
    }
}