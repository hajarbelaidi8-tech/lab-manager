package ma.fsm.lab_manager.utils;

import ma.fsm.lab_manager.entities.Publication;
import ma.fsm.lab_manager.repositories.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class FileIntegrityChecker {

    @Autowired
    private PublicationRepository publicationRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void checkAndFixPublicationFiles() {
        System.out.println("LOG: Demarrage de la verification d'integrite des fichiers PDF...");
        List<Publication> pubs = publicationRepo.findAll();
        int fixes = 0;

        for (Publication p : pubs) {
            String chemin = p.getCheminFichier();
            if (chemin != null && !chemin.isEmpty()) {
                Path filePath = Paths.get("uploads").resolve(chemin);
                if (!Files.exists(filePath)) {
                    System.out.println("LOG: Fichier manquant detecte : " + chemin + ". Generation d'un dummy...");
                    
                    // Utiliser le generateur existant
                    // On enleve "uploads/" car le generateur l'ajoute deja
                    PdfGenerator.generateDummyPdf(chemin, p.getTitre());
                    fixes++;
                }
            }
        }

        if (fixes > 0) {
            System.out.println("LOG: Verification terminee. " + fixes + " fichiers PDF ont ete restaures/generes.");
        } else {
            System.out.println("LOG: Verification terminee. Tous les fichiers sont presents.");
        }
    }
}
