package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Commentaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String contenu;

    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne
    private Membre auteur;

    @ManyToOne
    private Publication publication;

    @ManyToOne
    private Document document;
}