package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Reunion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private LocalDate dateReunion;
    @Column(length = 2000)
    private String ordreDuJour;

    private String pvPath; // Chemin direct pour la démo

    @OneToOne
    private Document pv;
}