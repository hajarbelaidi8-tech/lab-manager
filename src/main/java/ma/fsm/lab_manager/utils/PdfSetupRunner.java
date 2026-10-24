package ma.fsm.lab_manager.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class PdfSetupRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("LOG: Demarrage de l'utilitaire de generation de PDF et mise a jour de la base de donnees...");

        // 1. S'assurer que le dossier uploads existe
        Files.createDirectories(Paths.get("uploads"));

        // 2. Definir les fichiers a generer
        String[][] filesToGenerate = {
            {"dummy_pub_1.pdf", "Deep Learning for Medical Imaging"},
            {"dummy_pub_2.pdf", "Transformers in NLP: A Review"},
            {"dummy_pv_reunion.pdf", "PV Reunion de Lancement"},
            {"dummy_rapport.pdf", "Rapport d'Activite Annuel 2026"},
            {"dummy_convention.pdf", "Convention Partenariat OCP"}
        };

        for (String[] fileInfo : filesToGenerate) {
            String filename = fileInfo[0];
            String title = fileInfo[1];
            File file = new File("uploads/" + filename);
            if (!file.exists()) {
                PdfGenerator.generateDummyPdf(filename, title);
            } else {
                System.out.println("LOG: Le fichier " + filename + " existe deja.");
            }
        }

        // 3. Mise a jour de la base de donnees pour lier les fichiers
        try {
            // Lier aux publications
            List<Map<String, Object>> pubs = jdbcTemplate.queryForList("SELECT id FROM publication LIMIT 2");
            if (!pubs.isEmpty()) {
                jdbcTemplate.update("UPDATE publication SET chemin_fichier = ? WHERE id = ?", "dummy_pub_1.pdf", pubs.get(0).get("id"));
                if (pubs.size() > 1) {
                    jdbcTemplate.update("UPDATE publication SET chemin_fichier = ? WHERE id = ?", "dummy_pub_2.pdf", pubs.get(1).get("id"));
                }
            }

            // Lier aux reunions
            List<Map<String, Object>> reunions = jdbcTemplate.queryForList("SELECT id FROM reunion LIMIT 1");
            if (!reunions.isEmpty()) {
                jdbcTemplate.update("UPDATE reunion SET pv_path = ? WHERE id = ?", "dummy_pv_reunion.pdf", reunions.get(0).get("id"));
            }

            // Lier aux documents generaux
            List<Map<String, Object>> docs = jdbcTemplate.queryForList("SELECT id FROM document WHERE type_document = 'CONVENTION' LIMIT 1");
            if (!docs.isEmpty()) {
                jdbcTemplate.update("UPDATE document SET chemin_fichier = ? WHERE id = ?", "dummy_convention.pdf", docs.get(0).get("id"));
            }

            System.out.println("LOG: Mise a jour de la base de donnees terminee avec succes.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise a jour de la base de donnees : " + e.getMessage());
        }
    }
}
