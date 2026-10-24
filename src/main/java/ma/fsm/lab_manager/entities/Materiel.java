package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Materiel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String designation;
    private String marque;
    private String numeroSerie;
    private String type; // Serveur, GPU, PC, Stockage
    private LocalDate dateAcquisition;

    @ManyToOne
    @JsonBackReference
    private Membre detenteur; // Qui l'utilise actuellement (Section 14)

    @OneToOne
    private Document documentAchat; // Facture ou Bon de commande (Section 15)
}