package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Affiliation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin; // Si c'est vide (null), le membre est toujours actif

    @ManyToOne
    @JsonBackReference
    private Membre membre;
}