package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.TypeDocument;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired private MembreRepository membreRepo;
    @Autowired private PublicationRepository pubRepo;
    @Autowired private EquipeRepository equipeRepo;
    @Autowired private DemandeAdhesionRepository demandeRepo;
    @Autowired private MandatRepository mandatRepo;
    @Autowired private EvenementRepository evenementRepo;
    @Autowired private LaboratoireRepository laboRepo;
    @Autowired private ConventionRepository conventionRepo;
    @Autowired private DemandeMaterielRepository demandeMaterielRepo;
    @Autowired private MaterielRepository materielRepo;
    @Autowired private MessageRepository messageRepo;
    @Autowired private DocumentRepository documentRepo;
    @Autowired private ma.fsm.lab_manager.services.DocumentService documentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("equipes", equipeRepo.findAll());
        model.addAttribute("publications", pubRepo.findAll());
        model.addAttribute("evenements", evenementRepo.findAll());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, java.security.Principal principal) { // ⭐ ZID HAD L-PARAMETRE
        // 0. DARORI BACH L-NAVBAR MA-T-CRASH-ICH
        if (principal != null) {
            model.addAttribute("currentUser", membreRepo.findByEmail(principal.getName()));
        }
        // Infos Labo
        model.addAttribute("labo", laboRepo.findAll().stream().findFirst().orElse(null));

        // 1. ZONE STATISTIQUES (KPIs)
        model.addAttribute("totalMembres", membreRepo.count());
        model.addAttribute("totalEquipes", equipeRepo.count());

        long pubs2026 = pubRepo.findAll().stream()
                .filter(p -> p.getDatePublication() != null)
                .filter(p -> p.getDatePublication().getYear() == 2026)
                .count();
        model.addAttribute("totalPubs2026", pubs2026);

        long eventsAvenir = evenementRepo.findAll().stream()
                .filter(e -> e.getDateEvenement() != null)
                .filter(e -> e.getDateEvenement().isAfter(java.time.LocalDate.now()))
                .count();
        model.addAttribute("totalEventsAvenir", eventsAvenir);

        double budgetEstime = conventionRepo.findAll().stream()
                .mapToDouble(c -> c.getBudget() != null ? c.getBudget() : 0.0)
                .sum();
        model.addAttribute("totalBudget", budgetEstime);

        // Barre de progression (Simulation : 10% par membre, plafonné à 100%)
        long completion = Math.min(100, membreRepo.count() * 10);
        model.addAttribute("reportCompletion", completion);

        // 2. ZONE ACTION CENTER (Directeur)
        model.addAttribute("pendingAdhesions", demandeRepo.findAll().stream().filter(d -> !d.isTraitee()).collect(java.util.stream.Collectors.toList()));
        model.addAttribute("pendingMateriel", demandeMaterielRepo.findAll().stream().filter(d -> "EN_ATTENTE".equals(d.getStatut())).collect(java.util.stream.Collectors.toList()));

        // 3. ZONE MATÉRIEL & ÉQUITÉ
        java.util.List<ma.fsm.lab_manager.entities.Materiel> allMateriel = materielRepo.findAll();
        java.util.List<ma.fsm.lab_manager.entities.Membre> membresSansMateriel = membreRepo.findAll().stream()
                .filter(m -> m.getStatut() != ma.fsm.lab_manager.entities.StatutMembre.ANCIEN && m.getStatut() != ma.fsm.lab_manager.entities.StatutMembre.RETRAITE)
                .filter(m -> allMateriel.stream().noneMatch(mat -> mat.getDetenteur() != null && mat.getDetenteur().getId().equals(m.getId())))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("equiteList", membresSansMateriel);

        // 4. ZONE MÉMOIRE & NOTIFICATIONS (Flux d'activités)
        // On simule un flux avec les derniers messages et conventions
        model.addAttribute("latestMessages", messageRepo.findAll().stream()
                .sorted(java.util.Comparator.comparing(ma.fsm.lab_manager.entities.Message::getDateEnvoi).reversed())
                .limit(5)
                .collect(java.util.stream.Collectors.toList()));

        return "dashboard";
    }

    @GetMapping("/archives")
    public String archives(Model model) {
        if (documentRepo.count() == 0) {
            initArchives();
        }
        return "archives";
    }

    private void initArchives() {
        // 1. RAPPORT
        saveDoc("Rapport Annuel 2026", "dummy_rapport.pdf", TypeDocument.RAPPORT);
        // 2. PV (ADMINISTRATIF)
        saveDoc("PV Réunion Bureau", "dummy_pv.pdf", TypeDocument.ADMINISTRATIF);
        // 3. FINANCEMENT
        saveDoc("Budget OCP 2025", "dummy_ocp.pdf", TypeDocument.DEMANDE_FINANCEMENT);
        // 4. PROGRAMME
        saveDoc("Planning Workshop IA", "dummy_prog.pdf", TypeDocument.PROGRAMME);
    }

    private void saveDoc(String t, String p, TypeDocument type) {
        ma.fsm.lab_manager.entities.Document d = new ma.fsm.lab_manager.entities.Document();
        d.setTitre(t); d.setCheminFichier(p); d.setTypeDocument(type);
        d.setDateUpload(java.time.LocalDateTime.now());
        documentRepo.save(d);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}