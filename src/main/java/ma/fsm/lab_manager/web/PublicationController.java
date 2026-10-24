package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.entities.Publication;
import ma.fsm.lab_manager.repositories.EquipeRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.repositories.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/publications")
public class PublicationController {

    @Autowired private PublicationRepository pubRepo;
    @Autowired private EquipeRepository equipeRepo;
    @Autowired private MembreRepository membreRepo;
    @Autowired private ma.fsm.lab_manager.services.AuditService auditService;

    @Autowired private ma.fsm.lab_manager.services.NotificationService notificationService;
    @Autowired private ma.fsm.lab_manager.services.PublicationService publicationService;

    // MÉTHODE D'INITIALISATION DU STOCKAGE
    private void initStorage() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/publications");
            if (!java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
            }
            // Generation du PDF de test par defaut si absent
            java.io.File testPdf = new java.io.File("uploads/publications/test_publication.pdf");
            if (!testPdf.exists()) {
                ma.fsm.lab_manager.utils.PdfGenerator.generateDummyPdf("publications/test_publication.pdf", "Publication de Test");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    public String liste(@RequestParam(required = false) Long auteurId,
                        @RequestParam(required = false) Integer annee,
                        @RequestParam(required = false) Long equipeId,
                        Model model, Principal principal) {
        
        if (principal != null) {
            model.addAttribute("currentUser", membreRepo.findByEmail(principal.getName()));
        }
        
        java.util.List<Publication> pubs = pubRepo.findAll();
        
        // On pourrait ajouter un check ici, mais c'est lourd pour une liste.
        // On va plutot s'assurer que le script de demarrage a fait son travail.
        
        if (auteurId != null) {
            pubs = pubs.stream().filter(p -> p.getAuteurs().stream().anyMatch(a -> a.getId().equals(auteurId))).collect(java.util.stream.Collectors.toList());
        }
        if (annee != null) {
            pubs = pubs.stream().filter(p -> p.getDatePublication().getYear() == annee).collect(java.util.stream.Collectors.toList());
        }
        if (equipeId != null) {
            pubs = pubs.stream().filter(p -> p.getEquipe() != null && p.getEquipe().getId().equals(equipeId)).collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("publications", pubs);
        model.addAttribute("equipes", equipeRepo.findAll());
        model.addAttribute("auteurs", membreRepo.findAll());
        return "ressources/publications";
    }
    @GetMapping("/supprimer/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DIRECTEUR', 'ADMIN')") // ⭐ SEUL LE DIRECTEUR ⭐
    public String supprimerPublication(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Publication p = pubRepo.findById(id).orElse(null);
        if (p != null) {
            // 1. Audit (Trace l-khidma)
            auditService.log("PUBLICATION_DELETED", "Suppression par Directeur: " + p.getTitre());

            // 2. Supprimer (L-mس7)
            pubRepo.delete(p);
            ra.addFlashAttribute("success", "La publication a été supprimée.");
        }
        return "redirect:/publications";
    }
    // ⭐ MATCH-I HAD L-URL BACH T-KOUN B7AL L-IMAGE (بلا S)
    // ⭐ MATCH-I L-URL DYAL L-IMAGE (بلا S) ⭐
    @GetMapping("/publication/{id}")
    public String voirDetails(@PathVariable Long id, Model model, java.security.Principal principal) {

        // 1. Jbed l-publication (ila malqahach i-3ti error sghira machi crash 500)
        Publication pub = pubRepo.findById(id).orElseThrow(() -> new RuntimeException("Publication introuvable"));

        // 2. Fix Lazy Loading (Bach les auteurs i-banou بلا mouchkil)
        if (pub.getAuteurs() != null) pub.getAuteurs().size();

        // 3. Logic: fileExists (Bach dak l-bouton l-khder i-khdem)
        boolean fileExists = false;
        if (pub.getCheminFichier() != null && !pub.getCheminFichier().isEmpty()) {
            java.nio.file.Path path = java.nio.file.Paths.get("uploads").resolve(pub.getCheminFichier());
            fileExists = java.nio.file.Files.exists(path);
        }

        // 4. Logic: canEdit (Bach s-siyyed i-qder i-modifier)
        boolean canEdit = false;
        if (principal != null) {
            Membre user = membreRepo.findByEmail(principal.getName());
            canEdit = publicationService.canUserEdit(pub, user);
        }

        // ⭐ S-SIRR: Match-i smiyat l-variables m3a l-HTML dyalk ⭐
        model.addAttribute("p", pub);       // Nta derti ${p.titre} f l-HTML, dakchi 3lach khass "p"
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("fileExists", fileExists);

        // ⭐ PATH DYAL L-FICHIER HTML ⭐
        return "public/publication_details";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("publication", new Publication());
        model.addAttribute("equipes", equipeRepo.findAll());
        model.addAttribute("tousLesMembres", membreRepo.findAll());
        return "ressources/form_publication";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Publication p,
                              @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
                              Principal principal) {
        initStorage();

        // ⭐ S-SIRR HNA: ILA KNTI KANC-RÉYI (ID NULL) ⭐
        if (p.getId() == null && principal != null) {
            // Nta aslan khtarti l-auteurs f l-HTML, Spring 3mmerhoum f 'p'
            // Walakin khassna darori n-ziidou nta (s-syed li m-connecti) m3ahom
            Membre moi = membreRepo.findByEmail(principal.getName());
            p.getAuteurs().add(moi);
        }

        if (file != null && !file.isEmpty()) {
            // SÉCURITÉ : Empêcher les fichiers corrompus (0 Ko)
            if (file.getSize() <= 0) {
                System.err.println("Upload rejete : fichier vide.");
                return "redirect:/publications?error=empty_file";
            }
            try (java.io.InputStream is = file.getInputStream()) {
                String fileName = "publications/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path path = java.nio.file.Paths.get("uploads").resolve(fileName);
                java.nio.file.Files.copy(is, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                p.setCheminFichier(fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        pubRepo.save(p);

        if (p.getId() == null) {
            // NOTIFICATION À TOUS
            String msg = "Nouvelle publication : " + p.getTitre();
            for (Membre m : membreRepo.findAll()) {
                notificationService.envoyerNotification(m, "Nouvelle Publication", msg);
            }
        }

        return "redirect:/publications";
    }

    @GetMapping("/modifier/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String modifierForm(@PathVariable Long id, Model model, Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        
        Membre user = membreRepo.findByEmail(principal.getName());
        Publication p = pubRepo.findById(id).orElse(null);
        
        if (p == null) {
            ra.addFlashAttribute("error", "La publication avec l'ID " + id + " n'existe pas.");
            return "redirect:/publications";
        }

        if (!publicationService.canUserEdit(p, user)) {
            ra.addFlashAttribute("error", "Vous n'avez pas le droit de modifier cette publication.");
            return "redirect:/publications";
        }

        // INITIALISATION MANUELLE DES RELATIONS LAZY (Fix Section 12 Crash)
        if (p.getAuteurs() != null) p.getAuteurs().size(); 
        if (p.getEquipe() != null) p.getEquipe().getNom();

        model.addAttribute("publication", p);
        model.addAttribute("equipes", equipeRepo.findAll());
        return "ressources/modifier_publication";
    }

    @PostMapping("/update")
    public String updatePublication(@ModelAttribute Publication p, 
                                   @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
                                   Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        initStorage();
        if (principal == null) return "redirect:/login";
        
        Membre user = membreRepo.findByEmail(principal.getName());
        Publication existing = pubRepo.findById(p.getId()).orElse(null);
        
        if (existing == null) return "redirect:/publications";

        if (!publicationService.canUserEdit(existing, user)) {
            ra.addFlashAttribute("error", "Action non autorisée.");
            return "redirect:/publications";
        }

        // Mise à jour des champs (Traçabilité Section 21)
        existing.setTitre(p.getTitre());
        existing.setRevue(p.getRevue());
        existing.setDatePublication(p.getDatePublication());
        existing.setEquipe(p.getEquipe());
        existing.setDoi(p.getDoi());

        if (file != null && !file.isEmpty()) {
            try (java.io.InputStream is = file.getInputStream()) {
                String fileName = "publications/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path path = java.nio.file.Paths.get("uploads").resolve(fileName);
                java.nio.file.Files.copy(is, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                existing.setCheminFichier(fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        pubRepo.save(existing);
        return "redirect:/publications";
    }
}
