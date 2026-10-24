package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Laboratoire;
import ma.fsm.lab_manager.repositories.DemandeAdhesionRepository;
import ma.fsm.lab_manager.repositories.DemandeMaterielRepository;
import ma.fsm.lab_manager.repositories.EquipeRepository;
import ma.fsm.lab_manager.repositories.LaboratoireRepository;
import ma.fsm.lab_manager.services.MembreService;
import ma.fsm.lab_manager.services.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/directeur")
public class DirecteurController {

    @Autowired private DemandeAdhesionRepository adhesionRepo;
    @Autowired private DemandeMaterielRepository demandeMaterielRepo;
    @Autowired private EquipeRepository equipeRepo;
    @Autowired private LaboratoireRepository laboRepo;
    @Autowired private MembreService membreService;
    @Autowired private RessourceService ressourceService;

    @GetMapping("/demandes")
    public String voirDemandes(Model model) {
        model.addAttribute("demandes", adhesionRepo.findAll());
        model.addAttribute("demandesMateriel", demandeMaterielRepo.findAll());
        model.addAttribute("equipes", equipeRepo.findAll());
        return "directeur/liste_demandes";
    }


    @PostMapping("/demandes/accepter")
    public String accepter(@RequestParam Long demandeId) {
        membreService.accepterCandidat(demandeId);
        return "redirect:/directeur/demandes";
    }

    // ACTION DE REFUSER (Section 6 du PDF)
    @PostMapping("/demandes/refuser")
    public String refuser(@RequestParam Long demandeId) {
        membreService.refuserCandidat(demandeId);
        return "redirect:/directeur/demandes";
    }

    @PostMapping("/materiel/valider")
    public String validerMateriel(@RequestParam Long demandeId, @RequestParam boolean accepter) {
        ressourceService.validerDemandeMateriel(demandeId, accepter);
        return "redirect:/directeur/demandes";
    }
}