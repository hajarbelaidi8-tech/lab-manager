package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Equipe;
import ma.fsm.lab_manager.entities.Membre;
import ma.fsm.lab_manager.repositories.EquipeRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/equipes")
public class EquipeController {

    @Autowired
    private EquipeRepository equipeRepo;

    @Autowired
    private MembreRepository membreRepo;

    @GetMapping("/{id}")
    public String membersByTeam(@PathVariable Long id, 
                               @RequestParam(required = false) Long fromPublication,
                               Model model) {
        Equipe equipe = equipeRepo.findById(id).orElseThrow(() -> new RuntimeException("Equipe introuvable"));
        List<Membre> membres = membreRepo.findByEquipeId(id);
        
        model.addAttribute("equipe", equipe);
        model.addAttribute("listeMembres", membres);
        model.addAttribute("isTeamView", true);
        model.addAttribute("fromPublicationId", fromPublication);
        
        return "membres";
    }
}
