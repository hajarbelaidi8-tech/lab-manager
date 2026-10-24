package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.Document;
import ma.fsm.lab_manager.entities.TypeDocument;
import ma.fsm.lab_manager.repositories.DocumentRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    @Autowired private DocumentRepository documentRepository;
    @Autowired private ma.fsm.lab_manager.repositories.PublicationRepository publicationRepository;
    @Autowired private ma.fsm.lab_manager.repositories.ReunionRepository reunionRepository;
    @Autowired private AuditService auditService;

    private final Tika tika = new Tika();

    public Map<Integer, Map<String, Map<String, List<Document>>>> getHierarchicalArchives(String type) {
        List<Document> allDocs = new ArrayList<>();

        // ⭐ 1. TASHIH L-FILTRES (Mapping Front-to-Back)
        String dbType = type;
        if ("PV".equalsIgnoreCase(type)) dbType = "ADMINISTRATIF";
        if ("PUBLICATION".equalsIgnoreCase(type)) dbType = "RAPPORT";

        if (dbType != null && !dbType.isEmpty() && !dbType.equalsIgnoreCase("ALL")) {
            allDocs.addAll(documentRepository.findByTypeDocument(dbType));
        } else {
            allDocs.addAll(documentRepository.findAll());
        }

        // 2. Collecte des Publications (converties en RAPPORT)
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("PUBLICATION") || type.equalsIgnoreCase("RAPPORT")) {
            publicationRepository.findAll().forEach(p -> {
                if (p.getCheminFichier() != null && !p.getCheminFichier().isEmpty()) {
                    Document v = new Document();
                    v.setId(p.getId() + 1000000L); v.setTitre(p.getTitre());
                    v.setCheminFichier(p.getCheminFichier()); v.setTypeDocument(TypeDocument.RAPPORT);
                    v.setDateUpload(p.getDatePublication() != null ? p.getDatePublication().atStartOfDay() : java.time.LocalDateTime.now());
                    allDocs.add(v);
                }
            });
        }

        // 3. Collecte des Réunions (converties en ADMINISTRATIF)
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("PV") || type.equalsIgnoreCase("ADMINISTRATIF")) {
            reunionRepository.findAll().forEach(r -> {
                if (r.getPvPath() != null && !r.getPvPath().isEmpty()) {
                    Document v = new Document();
                    v.setId(r.getId() + 2000000L); v.setTitre("PV : " + r.getTitre());
                    v.setCheminFichier(r.getPvPath()); v.setTypeDocument(TypeDocument.ADMINISTRATIF);
                    v.setDateUpload(r.getDateReunion() != null ? r.getDateReunion().atStartOfDay() : java.time.LocalDateTime.now());
                    allDocs.add(v);
                }
            });
        }

        // ⭐ 4. L-GROUPING L-HIÉRARCHIQUE (Section 10 Match)
        return allDocs.stream()
                .collect(Collectors.groupingBy(
                        d -> (d.getEvenement() != null) ? d.getEvenement().getDateEvenement().getYear() :
                                (d.getDateUpload() != null ? d.getDateUpload().getYear() : 2026),
                        Collectors.groupingBy(
                                d -> (d.getEvenement() != null) ? "Activités : " + d.getEvenement().getTitre() : "Archives Générales",
                                Collectors.groupingBy(d -> d.getTypeDocument().name(), Collectors.toList())
                        )
                ));
    }

    // ⭐ 5. TASHIH L-RECHERCHE (Anti-Crash Logic)
    public List<Document> fullTextSearch(String query) {
        return documentRepository.findAll().stream().filter(doc -> {
            try {
                if (doc.getCheminFichier() == null) return doc.getTitre().toLowerCase().contains(query.toLowerCase());

                // Clean the path to avoid double "uploads/"
                String cleanPath = doc.getCheminFichier().replace("uploads/", "");
                File file = Paths.get("uploads").resolve(cleanPath).toFile();

                if (!file.exists()) return doc.getTitre().toLowerCase().contains(query.toLowerCase());

                String content = tika.parseToString(file);
                return content.toLowerCase().contains(query.toLowerCase()) || doc.getTitre().toLowerCase().contains(query.toLowerCase());
            } catch (Exception e) {
                return doc.getTitre().toLowerCase().contains(query.toLowerCase()); // Fallback sur le titre
            }
        }).collect(Collectors.toList());
    }
}