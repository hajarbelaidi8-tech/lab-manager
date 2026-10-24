package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.repositories.EvenementRepository;
import ma.fsm.lab_manager.repositories.PublicationRepository;
import ma.fsm.lab_manager.repositories.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {
    @Autowired private EvenementRepository eventRepo;
    @Autowired private PublicationRepository pubRepo;
    @Autowired private MembreRepository membreRepo;

    @Autowired private ma.fsm.lab_manager.repositories.MaterielRepository materielRepo;

    public Map<String, Object> genererRapportAnnuel(int annee) {
        Map<String, Object> rapport = new HashMap<>();
        rapport.put("annee", annee);
        
        long nbMembresActifs = membreRepo.findAll().stream()
                .filter(m -> m.getStatut() == ma.fsm.lab_manager.entities.StatutMembre.PERMANENT 
                          || m.getStatut() == ma.fsm.lab_manager.entities.StatutMembre.ASSOCIE
                          || m.getStatut() == ma.fsm.lab_manager.entities.StatutMembre.DOCTORANT)
                .count();
        rapport.put("nbMembres", nbMembresActifs);
        
        long nbEvents = eventRepo.findAll().stream()
                .filter(e -> e.getDateEvenement() != null && e.getDateEvenement().getYear() == annee)
                .count();
        rapport.put("nbEvenements", nbEvents);

        long nbPubs = pubRepo.findAll().stream()
                .filter(p -> p.getDatePublication() != null && p.getDatePublication().getYear() == annee)
                .count();
        rapport.put("nbPublications", nbPubs);

        // SCORE DE PERFORMANCE (Section 11 ⭐)
        long score = (nbPubs * 10) + (nbEvents * 5);
        rapport.put("scorePerformance", score);

        // SECTION 11 & 14 : INVENTAIRE MATÉRIEL
        rapport.put("materielTotal", materielRepo.count());
        rapport.put("materielAffecte", materielRepo.findAll().stream().filter(m -> m.getDetenteur() != null).count());
        rapport.put("listeMateriel", materielRepo.findAll());

        return rapport;
    }
}