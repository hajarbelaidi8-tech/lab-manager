package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Reunion;
import ma.fsm.lab_manager.entities.StatutMembre;
import ma.fsm.lab_manager.entities.TypeDocument;
import ma.fsm.lab_manager.services.GouvernanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reunions")
public class ReunionController {
    @Autowired private GouvernanceService gouvernanceService;

    @Autowired private ma.fsm.lab_manager.repositories.DocumentRepository documentRepo;
    @Autowired private ma.fsm.lab_manager.repositories.ReunionRepository reunionRepo;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("reunions", gouvernanceService.listerReunions());
        return "ressources/reunions";
    }

    @PostMapping("/organiser")
    public String organiser(Reunion r) {
        gouvernanceService.organiserReunion(r);
        return "redirect:/reunions";
    }

    @Autowired private ma.fsm.lab_manager.repositories.MembreRepository membreRepo;
    @Autowired private ma.fsm.lab_manager.services.NotificationService notificationService;

    @PostMapping("/pv/upload")
    public String uploaderPV(@RequestParam Long reunionId, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        if (!file.isEmpty()) {
            Reunion r = reunionRepo.findById(reunionId).orElseThrow();
            String fileName = System.currentTimeMillis() + "_PV_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);
            
            try (java.io.InputStream is = file.getInputStream()) {
                java.nio.file.Files.copy(is, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            ma.fsm.lab_manager.entities.Document doc = new ma.fsm.lab_manager.entities.Document();
            doc.setTitre("PV: " + r.getTitre());
            doc.setCheminFichier(fileName);
            doc.setTypeDocument(TypeDocument.ADMINISTRATIF);
            documentRepo.save(doc);
            
            r.setPv(doc);
            r.setPvPath(fileName); // Archivage du chemin direct
            reunionRepo.save(r);

            // NOTIFICATION
            String msg = "PV de réunion disponible : " + r.getTitre();
            for (ma.fsm.lab_manager.entities.Membre m : membreRepo.findAll()) {
                // Il faudrait ajouter un IF ici
                if (m.getStatut() != StatutMembre.DOCTORANT) {
                    notificationService.envoyerNotification(m, "Nouveau PV", msg);
                }
            }
        }
        return "redirect:/reunions";
    }
}