package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MembreService {

    @Autowired private MembreRepository membreRepo;
    @Autowired private EquipeRepository equipeRepo;
    @Autowired private DemandeAdhesionRepository adhesionRepo;
    @Autowired private AffiliationRepository affiliationRepo;
    @Autowired private PublicationRepository pubRepo;
    @Autowired private EvenementRepository evenementRepo;
    @Autowired private DocumentRepository documentRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private NotificationService notificationService;

    @Autowired private MandatRepository mandatRepo;
    @Autowired private AuditService auditService;

    public List<Membre> listerTousLesMembres() { return membreRepo.findAll(); }

    public void nommerDirecteur(Long membreId) {
        Membre nouveau = membreRepo.findById(membreId).orElseThrow();
        if (nouveau.getRole() == RoleMembre.DIRECTEUR) return;

        // 1. Clôturer l'ancien mandat du directeur actuel
        List<Membre> directeursActuels = membreRepo.findByRole(RoleMembre.DIRECTEUR);
        for (Membre oldDir : directeursActuels) {
            List<Mandat> mandatsActifs = mandatRepo.findByMembreAndTypeRoleAndDateFinIsNull(oldDir, RoleMembre.DIRECTEUR);
            for (Mandat m : mandatsActifs) {
                m.setDateFin(LocalDate.now());
                mandatRepo.save(m);
            }
            oldDir.setRole(RoleMembre.MEMBRE_EQUIPE); // Rétrogradation automatique pour l'exemple
            membreRepo.save(oldDir);
        }

        // 2. Créer le nouveau mandat
        Mandat m = new Mandat();
        m.setMembre(nouveau);
        m.setTypeRole(RoleMembre.DIRECTEUR);
        m.setDateDebut(LocalDate.now());
        mandatRepo.save(m);

        nouveau.setRole(RoleMembre.DIRECTEUR);
        membreRepo.save(nouveau);
        
        auditService.log("NOMMAGE_DIRECTEUR", "Nouveau directeur: " + nouveau.getPrenom() + " " + nouveau.getNom());
        notificationService.envoyerNotification(nouveau, "Changement de rôle", "Vous avez été nommé Directeur du laboratoire.");
    }

    public void supprimerMembre(Long id) {
        Membre m = membreRepo.findById(id).orElseThrow();
        // MÉMOIRE INSTITUTIONNELLE : AUCUNE SUPPRESSION PHYSIQUE
        if (m.getStatut() == StatutMembre.PERMANENT) {
            m.setStatut(StatutMembre.RETRAITE);
        } else {
            m.setStatut(StatutMembre.ANCIEN);
        }
        // ⭐ 2. Logic d Section 4.3 (Temporal History)
        // On clôture la période d'affiliation actuelle
        for (Affiliation aff : m.getAffiliations()) {
            if (aff.getDateFin() == null) {
                aff.setDateFin(LocalDate.now());
            }
        }

        // Ses publications restent liées à son ID, donc visibles
        membreRepo.save(m);
        auditService.log("ARCHIVAGE_MEMBRE", "Membre archivé: " + m.getPrenom() + " " + m.getNom() + " (Statut: " + m.getStatut() + ")");
        notificationService.envoyerNotification(m, "Statut du compte", "Votre compte a été archivé (Statut: " + m.getStatut() + ").");
    }

    public void changerRole(Long membreId, RoleMembre nouveauRole) {
        if (nouveauRole == RoleMembre.DIRECTEUR) {
            nommerDirecteur(membreId);
        } else {
            Membre m = membreRepo.findById(membreId).orElseThrow();
            if (m.getRole() != nouveauRole) {
                // Si c'était un chef d'équipe, on pourrait aussi gérer le relais ici,
                // mais la priorité est le Directeur.
                // ⭐ 1. CLÔTURER L'ANCIEN MANDAT (Sedd l-vwayaj l-9dim) ⭐
                // Nta aslan 3ndek had l-variable 'mandats' f Membre (li zedna qbila)
                for (Mandat oldMandat : m.getMandats()) {
                    if (oldMandat.getDateFin() == null) {
                        oldMandat.setDateFin(LocalDate.now()); // Sedd t-tarikh l-youm
                        mandatRepo.save(oldMandat);
                    }
                }
                Mandat mand = new Mandat();
                mand.setMembre(m);
                mand.setTypeRole(nouveauRole);
                mand.setDateDebut(LocalDate.now());
                mandatRepo.save(mand);
                m.setRole(nouveauRole);
                membreRepo.save(m);
                auditService.log("CHANGEMENT_ROLE", "Membre: " + m.getPrenom() + " " + m.getNom() + ", Nouveau rôle: " + nouveauRole);
            }
        }
    }

    public void accepterCandidat(Long demandeId) {
        DemandeAdhesion demande = adhesionRepo.findById(demandeId).orElseThrow();
        Equipe equipe = demande.getEquipeCible(); // L'équipe choisie par le candidat
        Membre m = new Membre();
        m.setNom(demande.getNom()); m.setPrenom(demande.getPrenom());
        m.setEmail(demande.getEmail()); m.setEquipe(equipe);
        //m.setDateNaissance(demande.getDateNaissance());
        m.setStatut(StatutMembre.PERMANENT); m.setRole(RoleMembre.MEMBRE_EQUIPE);
        m.setPassword(passwordEncoder.encode("123")); m.setDateEmbauche(LocalDate.now());
        membreRepo.save(m);
        // ⭐ ZID HADI: Ay wa7ed dkhul, kankhlsso ليه Mandat d "Membre d'équipe" s-ri3
        mandatRepo.save(new Mandat(null, RoleMembre.MEMBRE_EQUIPE, LocalDate.now(), null, m));
        affiliationRepo.save(new Affiliation(null, LocalDate.now(), null, m));
        
        // NOTIFICATION
        notificationService.envoyerNotification(m, "Compte chercheur", "Votre compte chercheur a été créé.");
        
        // Notification générale pour tous (Optionnel mais recommandé pour montrer le dynamisme)
        String msg = "Nouveau membre : " + m.getPrenom() + " " + m.getNom();
        for (Membre target : membreRepo.findAll()) {
            if (!target.getId().equals(m.getId())) {
                notificationService.envoyerNotification(target, "Annuaire", msg);
            }
        }

        demande.setTraitee(true); demande.setAcceptee(true);
        adhesionRepo.save(demande);
        auditService.log("ADHESION_ACCEPTEE", "Candidat: " + m.getPrenom() + " " + m.getNom() + " (Email: " + m.getEmail() + ")");
    }

    public void refuserCandidat(Long demandeId) {
        DemandeAdhesion demande = adhesionRepo.findById(demandeId).orElseThrow();
        demande.setTraitee(true); demande.setAcceptee(false);
        adhesionRepo.save(demande);
        auditService.log("ADHESION_REFUSEE", "Candidat: " + demande.getPrenom() + " " + demande.getNom() + " (Email: " + demande.getEmail() + ")");
    }

    public void mettreAJourMembre(Long id, String bio, String interets, StatutMembre statut) {
        Membre m = membreRepo.findById(id).orElseThrow();
        m.setBiographie(bio); m.setCentresInteret(interets); m.setStatut(statut);
        membreRepo.save(m);
    }

    // --- CETTE MÉTHODE RÈGLE TON ERREUR DANS RESSOURCECONTROLLER ---
    public String genererStatsRapport() {
        return membreRepo.count() + " Membres, " +
                evenementRepo.count() + " Événements, " +
                pubRepo.count() + " Publications.";
    }

    // --- RECHERCHE GLOBALE ---
    public List<Membre> chercherMembres(String q) {
        String lowerQ = q.toLowerCase();
        return membreRepo.findAll().stream()
                .filter(m ->
                        // Recherche par Nom ou Prénom
                        m.getNom().toLowerCase().contains(lowerQ) ||
                                m.getPrenom().toLowerCase().contains(lowerQ) ||
                                // RECHERCHE PAR ÉQUIPE (Nouveau ⭐)
                                (m.getEquipe() != null && (
                                        m.getEquipe().getNom().toLowerCase().contains(lowerQ) ||
                                                m.getEquipe().getAcronyme().toLowerCase().contains(lowerQ)
                                ))
                )
                .collect(Collectors.toList());
    }
    public List<Evenement> chercherEvenements(String q) {
        String lowerQ = q.toLowerCase();
        return evenementRepo.findAll().stream()
                .filter(e -> e.getTitre().toLowerCase().contains(lowerQ) || (e.getDescription() != null && e.getDescription().toLowerCase().contains(lowerQ)))
                .collect(Collectors.toList());
    }
    // --- PUBLICATIONS ---
    public List<Publication> getPublicationsByMembre(Membre m) {
        return pubRepo.findByAuteurs(m);
    }

    public List<Publication> chercherPubs(String q) {
        String lowerQ = q.toLowerCase();
        return pubRepo.findAll().stream()
                .filter(p -> p.getTitre().toLowerCase().contains(lowerQ) || 
                             p.getRevue().toLowerCase().contains(lowerQ) ||
                             p.getAuteurs().stream().anyMatch(a -> a.getNom().toLowerCase().contains(lowerQ) || a.getPrenom().toLowerCase().contains(lowerQ)))
                .collect(Collectors.toList());
    }
    public List<Document> chercherDocuments(String q) {
        String lowerQ = q.toLowerCase();
        return documentRepo.findAll().stream()
                .filter(d -> d.getTitre().toLowerCase().contains(lowerQ))
                .collect(Collectors.toList());
    }
}