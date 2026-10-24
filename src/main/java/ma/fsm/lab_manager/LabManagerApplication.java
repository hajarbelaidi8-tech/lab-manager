package ma.fsm.lab_manager;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class LabManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabManagerApplication.class, args);
	}

	@Bean
	CommandLineRunner start(EquipeRepository equipeRepo, MembreRepository membreRepo,
	                        MaterielRepository materielRepo, MandatRepository mandatRepo,
	                        AffiliationRepository affiliationRepo, LaboratoireRepository laboRepo,
	                        PublicationRepository pubRepo, AffectationRepository affectationRepo,
	                        PasswordEncoder passwordEncoder) {
		return args -> {
			if (laboRepo.count() == 0) {
				laboRepo.save(new Laboratoire(null, "Laboratoire LIAS", LocalDate.of(2005, 1, 1), "Innover pour un avenir intelligent"));
				String p123 = passwordEncoder.encode("123");

				// 1. ÉQUIPES (6 Equipes)
				Equipe ia = equipeRepo.save(Equipe.builder().nom("Intelligence Artificielle").acronyme("IA").description("Machine Learning").build());
				Equipe dsbd = equipeRepo.save(Equipe.builder().nom("Data Science").acronyme("DSBD").description("Analyse Big Data").build());
				Equipe iot = equipeRepo.save(Equipe.builder().nom("Internet des Objets").acronyme("IoT").description("Capteurs").build());
				Equipe cyber = equipeRepo.save(Equipe.builder().nom("Cybersécurité").acronyme("CYBER").description("Sécurité").build());
				Equipe embed = equipeRepo.save(Equipe.builder().nom("Systèmes Embarqués").acronyme("EMBED").description("Electronique").build());
				Equipe gl = equipeRepo.save(Equipe.builder().nom("Génie Logiciel").acronyme("GL").description("Architecture").build());

				// 2. LE DIRECTEUR (Driss ALAOUI)
				Membre dir = createFullMembre(dsbd, "ALAOUI", "Driss", "admin@univ.ma", p123, RoleMembre.DIRECTEUR, StatutMembre.PERMANENT, 1975, 2010, membreRepo, mandatRepo, affiliationRepo);

				// 3. LES 6 CHEFS D'EQUIPES (Un par équipe)
				Membre chefIA = createFullMembre(ia, "IDRISSI", "Ahmed", "chefia@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1980, 2018, membreRepo, mandatRepo, affiliationRepo);
				Membre chefDSBD = createFullMembre(dsbd, "MANSOURI", "Karim", "chefdsbd@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1982, 2016, membreRepo, mandatRepo, affiliationRepo);
				Membre chefIOT = createFullMembre(iot, "BENNANI", "Sanaa", "chefiot@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1985, 2017, membreRepo, mandatRepo, affiliationRepo);
				Membre chefCYBER = createFullMembre(cyber, "KASMI", "Youssef", "chefcyber@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1983, 2019, membreRepo, mandatRepo, affiliationRepo);
				Membre chefEMBED = createFullMembre(embed, "TOUIMI", "Hassan", "chefembed@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1984, 2020, membreRepo, mandatRepo, affiliationRepo);
				Membre chefGL = createFullMembre(gl, "DAOUDI", "Fatima", "chefgl@univ.ma", p123, RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 1988, 2021, membreRepo, mandatRepo, affiliationRepo);

				// 4. LES MEMBRES (Section 2.2)
				Membre yassine = createFullMembre(ia, "KACEMI", "Yassine", "doctorant@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.DOCTORANT, 1998, 2023, membreRepo, mandatRepo, affiliationRepo);
				Membre omar = createFullMembre(iot, "BELKADI", "Omar", "associe@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.ASSOCIE, 1990, 2020, membreRepo, mandatRepo, affiliationRepo);
				Membre meryem = createFullMembre(gl, "HAFSA", "Meryem", "hafsa@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.PERMANENT, 1995, 2022, membreRepo, mandatRepo, affiliationRepo);
				Membre layla = createFullMembre(cyber, "LAYLA", "Zineb", "layla@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.PERMANENT, 1996, 2024, membreRepo, mandatRepo, affiliationRepo);
                 //admin
				Membre systemAdmin = createFullMembre(null, "SYSTEM", "Admin", "admin@lab.ma", p123, RoleMembre.ADMIN, StatutMembre.PERMANENT, 1990, 2024, membreRepo, mandatRepo, affiliationRepo);
				// 5. LES ARCHIVES (Section 8 Match ⭐)
				Membre ret = createFullMembre(iot, "ZAHIRI", "Imane", "retraite@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.RETRAITE, 1960, 1990, membreRepo, mandatRepo, affiliationRepo);
				Membre anc = createFullMembre(ia, "EL FASSI", "Mohamed", "ancien@univ.ma", p123, RoleMembre.MEMBRE_EQUIPE, StatutMembre.ANCIEN, 1955, 1985, membreRepo, mandatRepo, affiliationRepo);

				// 6. PUBLICATIONS (ManyToMany Bidirectionnel & Sans Duplicate ⭐⭐⭐)

				// Pub IA: Ahmed + Yassine
				Publication p1 = createPublication("Deep Learning for Medical Imaging", "IEEE Access", 2024, ia);
				p1.getAuteurs().addAll(Arrays.asList(chefIA, yassine));
				pubRepo.save(p1);

				// Pub GL: Fatima + Meryem + Driss (Image 4 Match)
				Publication p2 = createPublication("Microservices Architecture: Best Practices", "Software Journal", 2024, gl);
				p2.getAuteurs().addAll(Arrays.asList(chefGL, meryem, dir));
				pubRepo.save(p2);

				// Pub IoT: Sanaa + Omar
				Publication p3 = createPublication("Sensors and IoT in Smart Cities", "Nature", 2023, iot);
				p3.getAuteurs().addAll(Arrays.asList(chefIOT, omar));
				pubRepo.save(p3);

				// Pub Cyber: Youssef + Layla
				Publication p4 = createPublication("Cybersecurity in Cloud Networks", "IEEE Security", 2024, cyber);
				p4.getAuteurs().addAll(Arrays.asList(chefCYBER, layla));
				pubRepo.save(p4);

				// Pub DSBD: Karim + Driss
				Publication p5 = createPublication("Advanced Machine Learning Algorithms", "IEEE Transactions", 2026, dsbd);
				p5.getAuteurs().addAll(Arrays.asList(chefDSBD, dir));
				pubRepo.save(p5);

				// 7. LE PARC MATÉRIEL (Section 14 & 15 Match ⭐⭐⭐)
				// Affecté
				materielRepo.save(new Materiel(null, "MacBook Pro M3", "APPLE", "S/N-001", "ORDINATEUR", LocalDate.of(2024, 1, 1), dir, null));
				materielRepo.save(new Materiel(null, "PC Dell XPS", "DELL", "S/N-998", "ORDINATEUR", LocalDate.of(2024, 4, 1), meryem, null));

				// LE STOCK (Disponible pour demandes)
				materielRepo.save(new Materiel(null, "Serveur NVIDIA A100 (Backup)", "NVIDIA", "GPU-STOCK-99", "SERVEUR", LocalDate.of(2024, 3, 1), null, null));
				materielRepo.save(new Materiel(null, "Imprimante LaserJET Pro", "HP", "PRNT-55", "IMPRIMANTE", LocalDate.of(2023, 11, 15), null, null));
				materielRepo.save(new Materiel(null, "Scanner Pro 3000", "CANON", "SCAN-02", "SCANNER", LocalDate.of(2024, 2, 20), null, null));
				materielRepo.save(new Materiel(null, "Vidéoprojecteur 4K", "EPSON", "PROJ-10", "PROJECTEUR", LocalDate.of(2024, 5, 10), null, null));

				initStorage();
				System.out.println("LOG: Initialisation 100% Logique et Complète Terminée.");
			}
		};
	}

	// --- HELPERS (PRO & SECURE) ---
	private Membre createFullMembre(Equipe eq, String nom, String prenom, String email, String pass, RoleMembre role, StatutMembre statut, int anNaiss, int anEmb, MembreRepository mR, MandatRepository manR, AffiliationRepository aR) {
		Membre m = new Membre();
		m.setNom(nom); m.setPrenom(prenom); m.setEmail(email); m.setPassword(pass);
		m.setStatut(statut); m.setRole(role); m.setEquipe(eq);
		m.setEtablissementOrigine("Université Hassan II"); m.setLaboratoireOrigine("LIAS Lab");
		// ⭐ MATCH-I HAD L-IF BACH MA-I-BQA-CH L-CRASH ⭐
		if (eq != null) {
			m.setBiographie("Expert(e) en " + eq.getNom() + ". Travaille sur l'axe " + eq.getDescription());
			m.setCentresInteret(eq.getAcronyme() + ", Recherche");
		} else {
			m.setBiographie("Administrateur technique du système.");
			m.setCentresInteret("Maintenance, Sécurité");
		}
		m.setDateNaissance(LocalDate.of(anNaiss, 1, 1));
		m.setDateEmbauche(LocalDate.of(anEmb, 9, 1));
		m.setDateAffiliation(LocalDate.of(anEmb, 10, 1));
		mR.save(m); // On sauve le membre

		// Historique (Dates clôturées pour Retraités/Anciens)
		LocalDate dateF = (statut == StatutMembre.RETRAITE || statut == StatutMembre.ANCIEN ? LocalDate.of(2023, 12, 31) : null);
		manR.save(new Mandat(null, role, m.getDateAffiliation(), dateF, m));
		aR.save(new Affiliation(null, m.getDateAffiliation(), dateF, m));
		return m;
	}

	private Publication createPublication(String titre, String revue, int annee, Equipe eq) {
		Publication p = new Publication();
		p.setTitre(titre); p.setRevue(revue); p.setEquipe(eq);
		p.setDatePublication(LocalDate.of(annee, 1, 1));
		p.setAuteurs(new HashSet<>());
		return p; // On ne sauve pas encore ! On sauve après avoir ajouté les auteurs.
	}

	private void initStorage() {
		try {
			java.nio.file.Path path = java.nio.file.Paths.get("uploads/publications");
			if (!java.nio.file.Files.exists(path)) java.nio.file.Files.createDirectories(path);
		} catch (Exception e) {}
	}
}