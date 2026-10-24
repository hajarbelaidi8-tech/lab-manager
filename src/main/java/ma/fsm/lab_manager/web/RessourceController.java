package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Affectation;
import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.entities.Materiel;
import ma.fsm.lab_manager.entities.TypeDocument;
import ma.fsm.lab_manager.repositories.MaterielRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import ma.fsm.lab_manager.repositories.PublicationRepository;
import ma.fsm.lab_manager.services.MembreService;
import ma.fsm.lab_manager.services.ReportService;
import ma.fsm.lab_manager.services.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RessourceController {

    @Autowired private MaterielRepository materielRepo;
    @Autowired private PublicationRepository pubRepo;
    @Autowired private MembreService membreService;
    @Autowired private ReportService reportService;
    @Autowired private RessourceService ressourceService;
    @Autowired private ma.fsm.lab_manager.repositories.DocumentRepository documentRepo;
    @Autowired private ma.fsm.lab_manager.services.DocumentService documentService;
    @Autowired private ma.fsm.lab_manager.repositories.AffectationRepository affectationRepo;
    @Autowired private MembreRepository membreRepo;

    @GetMapping("/materiel")
    public String listeMateriel(Model model) {
        List<Materiel> inventaire = materielRepo.findAll();
        List<Membre> tousLesMembres = membreService.listerTousLesMembres();
        
        // Liste des membres qui n'ont pas de matériel affecté (Section 14)
        List<Membre> membresSansMateriel = tousLesMembres.stream()
                .filter(m -> m.getStatut() != ma.fsm.lab_manager.entities.StatutMembre.ANCIEN && m.getStatut() != ma.fsm.lab_manager.entities.StatutMembre.RETRAITE)
                .filter(m -> inventaire.stream().noneMatch(mat -> mat.getDetenteur() != null && mat.getDetenteur().getId().equals(m.getId())))
                .collect(Collectors.toList());

        model.addAttribute("inventaire", inventaire);
        model.addAttribute("membresSansMateriel", membresSansMateriel);
        return "ressources/materiel";
    }

    @PostMapping("/materiel/demander")
    public String demander(@RequestParam Long materielId, @RequestParam String motif, java.security.Principal principal) {
        // ✅ ST3MEL PRINCIPAL BACH N-3ERFOU CHKOUN S-SYED LI GHALES QDAM L-ECRAN

        Membre m = membreRepo.findByEmail(principal.getName());
        ressourceService.demanderMateriel(materielId, m, motif);
        return "redirect:/materiel";
    }
    @GetMapping("/rapport")
    public String voirRapport(@RequestParam(defaultValue = "2026") int annee, Model model) {
        model.addAttribute("rapport", reportService.genererRapportAnnuel(annee));
        return "ressources/rapport";
    }

    @PostMapping("/materiel/inventaire/generer")
    public String genererInventaire(org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            int annee = java.time.LocalDate.now().getYear();
            String fileName = "Inventaire_Materiel_" + annee + "_" + System.currentTimeMillis() + ".pdf";
            String title = "Rapport d'Inventaire Annuel " + annee + " (LIAS Lab)";
            
            // 1. GÉNÉRATION PHYSIQUE (Section 14 & 15 ⭐)
            ma.fsm.lab_manager.utils.PdfGenerator.generateInventoryPdf(fileName, materielRepo.findAll());
            
            // 2. ARCHIVAGE AUTOMATIQUE (Section 15 - Catégorie ADMINISTRATIVE)
            ma.fsm.lab_manager.entities.Document doc = new ma.fsm.lab_manager.entities.Document();
            doc.setTitre(title);
            doc.setCheminFichier(fileName);
            doc.setTypeDocument(TypeDocument.ADMINISTRATIF);
            doc.setDateUpload(java.time.LocalDateTime.now());
            documentRepo.save(doc);
            
            ra.addFlashAttribute("successMessage", "Inventaire matériel archivé sous 'Documents Administratifs'.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Erreur lors de la génération : " + e.getMessage());
        }
        return "redirect:/materiel";
    }

    @PostMapping("/materiel/document/upload")
    public String uploadDocumentAchat(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                      @RequestParam("materielId") Long materielId,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            ma.fsm.lab_manager.entities.Materiel mat = materielRepo.findById(materielId).orElseThrow();
            if (!file.isEmpty()) {
                String fileName = "achat_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }
                java.nio.file.Path path = uploadDir.resolve(fileName);
                java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                ma.fsm.lab_manager.entities.Document doc = new ma.fsm.lab_manager.entities.Document();
                doc.setTitre("Facture/Achat : " + mat.getDesignation());
                doc.setCheminFichier(fileName);
                doc.setTypeDocument(TypeDocument.ADMINISTRATIF);
                doc.setDateUpload(java.time.LocalDateTime.now());
                documentRepo.save(doc);
                
                mat.setDocumentAchat(doc);
                materielRepo.save(mat);
                
                ra.addFlashAttribute("successMessage", "Document d'achat associé et archivé sous 'ADMINISTRATIF'.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Erreur upload : " + e.getMessage());
        }
        return "redirect:/materiel";
        }
    @PostMapping("/materiel/affecter")
    public String affecterMateriel(@RequestParam Long materielId, @RequestParam Long membreId) {
        Materiel mat = materielRepo.findById(materielId).orElseThrow();
        Membre m = membreRepo.findById(membreId).orElseThrow();

        // 1. Match-i l-vwayaj f l-makhzen
        mat.setDetenteur(m);
        materielRepo.save(mat);

        // 2. Historiser f table Affectation (Match Section 14)
        Affectation aff = new Affectation(null, mat, m, LocalDate.now(), null, "Affectation manuelle par direction");
        affectationRepo.save(aff);

        return "redirect:/materiel";
    }

    @PostMapping("/rapport/generer")
    public String genererEtArchiverRapport(@RequestParam int annee, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            String fileName = "Rapport_Scientifique_" + annee + "_" + System.currentTimeMillis() + ".pdf";
            String title = "Rapport d'Activité Scientifique " + annee + " (LIAS Lab)";

            // 1. GÉNÉRATION (Section 11 ⭐)
            ma.fsm.lab_manager.utils.PdfGenerator.generateDummyPdf(fileName, title + " - Score Performance : " + reportService.genererRapportAnnuel(annee).get("scorePerformance"));

            // 2. ARCHIVAGE (Catégorie RAPPORT)
            ma.fsm.lab_manager.entities.Document doc = new ma.fsm.lab_manager.entities.Document();
            doc.setTitre(title);
            doc.setCheminFichier(fileName);
            doc.setTypeDocument(TypeDocument.RAPPORT);
            doc.setDateUpload(java.time.LocalDateTime.of(annee, 12, 31, 23, 59));
            documentRepo.save(doc);

            ra.addFlashAttribute("successMessage", "Rapport scientifique " + annee + " archivé sous 'Rapports d'Activité'.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Erreur lors de l'archivage : " + e.getMessage());
        }
        return "redirect:/rapport?annee=" + annee;
    }
        }