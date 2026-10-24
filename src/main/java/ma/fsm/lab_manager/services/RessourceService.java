package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class RessourceService {
    @Autowired private MaterielRepository materielRepo;
    @Autowired private DemandeMaterielRepository demandeRepo;
    @Autowired private PublicationRepository pubRepo;
    @Autowired private NotificationService notificationService;
    @Autowired private AuditService auditService;
    @Autowired private AffectationRepository affectationRepo;

    public void demanderMateriel(Long materielId, Membre demandeur, String motif) {
        Materiel m = materielRepo.findById(materielId).orElseThrow();
        DemandeMateriel demande = new DemandeMateriel();
        demande.setMateriel(m);
        demande.setDemandeur(demandeur);
        demande.setMotif(motif);
        demande.setStatut("EN_ATTENTE");
        demandeRepo.save(demande);
        
        auditService.log("MATERIEL_REQUEST", "Membre: " + demandeur.getNom() + " pour " + m.getDesignation());
        notificationService.alertDirector("Besoin en matériel", demandeur.getNom() + " a exprimé un besoin pour : " + m.getDesignation());
    }

    public void validerDemandeMateriel(Long demandeId, boolean accepter) {
        // 1. Jbed d-demande men l-makhzen
        DemandeMateriel dm = demandeRepo.findById(demandeId).orElseThrow();

        if (accepter) {
            dm.setStatut("ACCEPTEE");

            // ⭐ 2. MATCH SECTION 14 : HISTORISATION ⭐
            // Kan-sajjlou l-vwayaj f table Affectation bach n-Match-iyou Section 14
            Affectation aff = new Affectation();
            aff.setMateriel(dm.getMateriel());
            aff.setMembre(dm.getDemandeur()); // Hna d-da99a: s-syed li tleb nit
            aff.setDateDebut(java.time.LocalDate.now());
            aff.setCommentaire("Affectation validée par direction.");
            affectationRepo.save(aff);

            // ⭐ 3. FIX L-MOUCHKIL D "DRISS ALAOUI" ⭐
            // Hna kankhdmou b 'dm.getDemandeur()' bach l-PC i-Match-i s-syed s7i7
            Materiel mat = dm.getMateriel();
            mat.setDetenteur(dm.getDemandeur());
            materielRepo.save(mat);

            // 4. NOTIFICATION & AUDIT (Section 19 & 21)
            notificationService.envoyerNotification(dm.getDemandeur(), "Matériel", "Votre demande pour " + mat.getDesignation() + " a été acceptée.");
            auditService.log("MATERIEL_ASSIGNED", "Matériel: " + mat.getDesignation() + " affecté à " + dm.getDemandeur().getNom());

        } else {
            // ILA L-MODIR REFUSA
            dm.setStatut("REFUSEE");
            notificationService.envoyerNotification(dm.getDemandeur(), "Matériel", "Demande refusée pour " + dm.getMateriel().getDesignation());
            auditService.log("MATERIEL_REJECTED", "ID Demande: " + demandeId);
        }

        // 5. Sajjel l-karar l-khir f table demande_materiel
        demandeRepo.save(dm);
    }

    public List<Publication> chercherPubs(String q) {
        String lowerQ = q.toLowerCase();
        return pubRepo.findAll().stream()
                .filter(p -> p.getTitre().toLowerCase().contains(lowerQ) || 
                             p.getAuteurs().stream().anyMatch(a -> a.getNom().toLowerCase().contains(lowerQ) || a.getPrenom().toLowerCase().contains(lowerQ)))
                .toList();
    }
}