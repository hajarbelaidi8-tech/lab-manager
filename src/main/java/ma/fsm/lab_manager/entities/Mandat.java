package ma.fsm.lab_manager.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Mandat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleMembre typeRole;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    @ManyToOne
    @JsonBackReference // ⭐ ZID HAD L-SṬER HNA ⭐
    private Membre membre;
}