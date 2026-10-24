package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.services.MembreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {
    @Autowired private MembreService membreService;

    @Autowired private ma.fsm.lab_manager.services.DocumentService documentService;

    @GetMapping("/recherche")
    public String chercher(@RequestParam String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("membres", membreService.chercherMembres(q));
        model.addAttribute("evenements", membreService.chercherEvenements(q));
        model.addAttribute("publications", membreService.chercherPubs(q));
        model.addAttribute("documents", documentService.fullTextSearch(q)); // RECHERCHE INTELLIGENTE (SECTION 18)
        return "recherche/resultats";
    }
}
