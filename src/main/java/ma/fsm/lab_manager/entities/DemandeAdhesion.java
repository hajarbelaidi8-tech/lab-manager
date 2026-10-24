package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class DemandeAdhesion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    @Column(length = 1000)
    private String motivation;
    private String cvPath;
    //private LocalDate dateNaissance;
    @ManyToOne
    private Equipe equipeCible;
    private LocalDate dateDemande = LocalDate.now();
    private boolean traitee = false;
    private boolean acceptee = false;
}