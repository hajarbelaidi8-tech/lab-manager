/*package ma.fsm.lab_manager.config;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initPublications(MembreRepository membreRepo, PublicationRepository pubRepo) {
        return args -> {
            System.out.println("LOG: Démarrage de la liaison manuelle des publications...");

            // 1. Assurer qu'une publication de test existe
            Publication samplePub = pubRepo.findAll().stream()
                    .filter(p -> p.getTitre().contains("Deep Learning for Medical Imaging"))
                    .findFirst()
                    .orElseGet(() -> {
                        Publication p = new Publication();
                        p.setTitre("Deep Learning for Medical Imaging");
                        p.setRevue("IEEE Access");
                        p.setDatePublication(LocalDate.now());
                        p.setAuteurs(new HashSet<>());
                        return pubRepo.save(p);
                    });

            // 2. Récupérer Fatima (par nom pour être sûr de l'ID)
            Membre fatima = membreRepo.findAll().stream()
                    .filter(m -> m.getNom().equalsIgnoreCase("DAOUDI"))
                    .findFirst()
                    .orElse(null);

            if (fatima != null) {
                System.out.println("LOG: Fatima trouvée (ID: " + fatima.getId() + "). Liaison avec la publication...");
                
                // On met à jour le côté "maître" de la relation (Publication.auteurs)
                if (samplePub.getAuteurs() == null) samplePub.setAuteurs(new HashSet<>());
                samplePub.getAuteurs().add(fatima);
                pubRepo.save(samplePub);
                
                // Pour être sûr que l'objet en session est à jour
                membreRepo.save(fatima);
                System.out.println("LOG: Liaison Fatima <-> Publication réussie !");
            } else {
                System.out.println("WARN: Fatima DAOUDI non trouvée en base.");
            }

            // 3. Brute-force optionnel : lier toutes les publications à tout le monde pour le test
            List<Publication> allPubs = pubRepo.findAll();
            List<Membre> allMembres = membreRepo.findAll();
            for (Publication p : allPubs) {
                if (p.getAuteurs() == null) p.setAuteurs(new HashSet<>());
         //       p.getAuteurs().addAll(allMembres);
                pubRepo.save(p);
            }
            System.out.println("LOG: Liaison brute-force terminée (Toutes les pubs liées à tous les membres).");
        };
    }
}
*/