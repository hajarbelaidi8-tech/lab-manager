package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GouvernanceService {
    @Autowired private PartenaireRepository partenaireRepo;
    @Autowired private ConventionRepository conventionRepo;
    @Autowired private ReunionRepository reunionRepo;
    @Autowired private MandatRepository mandatRepo;

    public void ajouterPartenaire(Partenaire p) { partenaireRepo.save(p); }
    public List<Partenaire> listerPartenaires() { return partenaireRepo.findAll(); }

    public void creerConvention(Convention c) { conventionRepo.save(c); }
    public List<Convention> listerConventions() { return conventionRepo.findAll(); }

    public void organiserReunion(Reunion r) { reunionRepo.save(r); }
    public List<Reunion> listerReunions() { return reunionRepo.findAll(); }

    public void historiserMandat(Mandat m) { mandatRepo.save(m); }
}