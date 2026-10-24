package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Document;
import ma.fsm.lab_manager.entities.TypeDocument; // ⭐ DARORI L-IMPORT
import ma.fsm.lab_manager.services.DocumentService;
import ma.fsm.lab_manager.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class DocumentRestController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentRestController.class);
    @Autowired private DocumentService documentService;
    @Autowired private DocumentRepository documentRepo;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                            @RequestParam("documentId") Long documentId) {
        try {
            Document doc = documentRepo.findById(documentId).orElse(null);
            if (doc == null) return ResponseEntity.notFound().build();

            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get("uploads").resolve(fileName);
                try (java.io.InputStream is = file.getInputStream()) {
                    java.nio.file.Files.copy(is, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                doc.setCheminFichier(fileName);
                documentRepo.save(doc);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Fichier vide");
        } catch (Exception e) {
            logger.error("Upload error", e);
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }

    // ⭐ 1. TASHIH L-VIGILE D L-PDF (Match Section 10 Security)
    @GetMapping("/view")
    public ResponseEntity<?> downloadFile(@RequestParam("path") String filePath, java.security.Principal principal) {
        try {
            Document doc = documentRepo.findFirstByCheminFichier(filePath);

            // ILA KAN L-WRQA SIRRIYA W S-SYED MA-M-CONNECTI-CH
            if (doc != null && principal == null) {
                TypeDocument type = doc.getTypeDocument(); // ST3MEL L-ENUM HNA

                // ✅ R-Rbet m3a l-9awanin d s-salama
                if (type == TypeDocument.RAPPORT || type == TypeDocument.ADMINISTRATIF) {
                    return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                            .header(HttpHeaders.LOCATION, "/login")
                            .build();
                }
            }

            String cleanPath = filePath.replace("uploads/", "");
            String baseDir = System.getProperty("user.dir") + "/uploads/";
            Path targetPath = Paths.get(baseDir).resolve(cleanPath).normalize().toAbsolutePath();

            Resource resource = new UrlResource(targetPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body("Fichier introuvable");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }

    // ⭐ 2. TASHIH L-DTo (Bach d-data t- Match-i l-Frontend)
    public static class DocumentDTO {
        public Long id;
        public String titre;
        public String typeDocument; // Hna kankhlliwh String bach l-React/Angular i-fhmouh
        public String cheminFichier;
        public String nomEvenement;
        public String dateUpload;

        public DocumentDTO(Document d) {
            this.id = d.getId();
            this.titre = d.getTitre();
            // ✅ N-Gaddi-w l-Enum l String automático
            this.typeDocument = (d.getTypeDocument() != null) ? d.getTypeDocument().name() : "AUTRE";
            this.cheminFichier = d.getCheminFichier();
            this.nomEvenement = (d.getEvenement() != null) ? d.getEvenement().getTitre() : "Archive Générale";
            this.dateUpload = d.getDateUpload() != null ? d.getDateUpload().toString() : "";
        }

        public DocumentDTO(Long id, String titre, String type, String path, String event, String date) {
            this.id = id; this.titre = titre; this.typeDocument = type;
            this.cheminFichier = path; this.nomEvenement = event; this.dateUpload = date;
        }
    }

    @GetMapping(value = "/hierarchy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHierarchy(@RequestParam(required = false) String type) {
        try {
            Map<Integer, Map<String, Map<String, List<Document>>>> rawHierarchy = documentService.getHierarchicalArchives(type);
            if (rawHierarchy == null) return ResponseEntity.ok(Collections.emptyMap());

            // Conversion en DTO bach n-fekk-houm l-mouchkil d l-Infinite Loop
            Map<Integer, Map<String, Map<String, List<DocumentDTO>>>> dtoHierarchy = new HashMap<>();
            rawHierarchy.forEach((year, events) -> {
                Map<String, Map<String, List<DocumentDTO>>> eventMap = new HashMap<>();
                events.forEach((eventName, types) -> {
                    Map<String, List<DocumentDTO>> typeMap = new HashMap<>();
                    types.forEach((docType, docs) -> {
                        List<DocumentDTO> dtoList = new ArrayList<>();
                        docs.forEach(d -> dtoList.add(new DocumentDTO(d)));
                        typeMap.put(docType, dtoList);
                    });
                    eventMap.put(eventName, typeMap);
                });
                dtoHierarchy.put(year, eventMap);
            });
            return ResponseEntity.ok(dtoHierarchy);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyMap());
        }
    }
}