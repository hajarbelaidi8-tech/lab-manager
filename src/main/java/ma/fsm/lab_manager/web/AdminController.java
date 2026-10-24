package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Laboratoire;
import ma.fsm.lab_manager.repositories.HistoriqueRepository;
import ma.fsm.lab_manager.repositories.LaboratoireRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ⭐ IMPORT S7I7 ⭐
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private MembreRepository membreRepo;
    @Autowired private HistoriqueRepository historiqueRepo;
    @Autowired private LaboratoireRepository laboRepo;

    @GetMapping("/utilisateurs")
    public String gererComptes(Model model) {
        model.addAttribute("membres", membreRepo.findAll());
        return "admin/comptes";
    }

    @GetMapping("/audit")
    public String voirMaintenance(Model model) {
        model.addAttribute("logs", historiqueRepo.findAll());
        return "admin/audit";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        Laboratoire lab = laboRepo.findAll().stream().findFirst().orElse(new Laboratoire());
        model.addAttribute("labo", lab);
        return "admin/settings";
    }

    @PostMapping("/settings/save")
    public String saveSettings(@ModelAttribute Laboratoire labo) {
        laboRepo.save(labo);
        return "redirect:/admin/settings?success";
    }
}