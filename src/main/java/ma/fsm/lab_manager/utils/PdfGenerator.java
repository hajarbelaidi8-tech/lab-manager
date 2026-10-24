package ma.fsm.lab_manager.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import ma.fsm.lab_manager.entities.Materiel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static void generateDummyPdf(String path, String content) {
        generatePdf(path, content);
    }

    public static void generateInventoryPdf(String path, List<Materiel> materiels) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport d'Inventaire Materiel\n");
        sb.append("-----------------------------\n");
        for (Materiel m : materiels) {
            sb.append("- ").append(m.getDesignation()).append(" (")
              .append(m.getMarque()).append(" - S/N: ")
              .append(m.getNumeroSerie()).append(")\n");
        }
        generatePdf(path, sb.toString());
    }

    private static void generatePdf(String path, String content) {
        try {
            // S'assurer que le dossier parent existe
            File file = new File("uploads/" + path);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(100, 700);
                    
                    String[] lines = sanitize(content).split("\n");
                    for (String line : lines) {
                        try {
                            contentStream.showText(line);
                        } catch (IllegalArgumentException e) {
                            // En cas de caractères non supportés par Helvetica
                            contentStream.showText("???");
                        }
                        contentStream.newLine();
                    }
                    contentStream.endText();
                }

                document.save(file);
                System.out.println("PDF créé avec succès : " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    /**
     * Nettoie le texte pour éviter les erreurs de PDFBox (control chars).
     * On garde les \n pour le découpage en lignes interne.
     */
    private static String sanitize(String text) {
        if (text == null) return "";
        // On remplace les \r par rien, et on garde \n pour notre split
        return text.replace("\r", "").trim();
    }
}
