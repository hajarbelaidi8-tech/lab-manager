package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class DemandeMateriel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateDemande = LocalDate.now();
    private String statut; // EN_ATTENTE, ACCEPTEE, REFUSEE
    private String motif;//Pourquoi il en a besoin?
    private LocalDate dateLivraison; // Date de remise effective du matériel

    @ManyToOne
    private Materiel materiel;

    @ManyToOne
    private Membre demandeur;
}