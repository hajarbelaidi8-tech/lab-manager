package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Affectation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Materiel materiel;

    @ManyToOne
    private Membre membre;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String commentaire;
}
