package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Historique {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action; // Ex: "MESSAGE_ENVOYE", "DOCUMENT_UPLOAD"
    private String details;
    private LocalDateTime dateAction = LocalDateTime.now();

    @ManyToOne
    private Membre utilisateur;
}