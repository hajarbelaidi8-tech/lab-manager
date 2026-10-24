package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Convention {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Column(length = 1000)
    private String description;
    private LocalDate dateSignature;
    private LocalDate dateFin;
    private Double budget;

    @ManyToOne
    private Partenaire partenaire;

    @OneToOne
    private Document document;
}