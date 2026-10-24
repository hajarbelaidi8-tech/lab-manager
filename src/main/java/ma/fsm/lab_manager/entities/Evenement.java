package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Set;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Evenement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private LocalDate dateEvenement;
    private String lieu;
    private String type; // CONFERENCE, SEMINAIRE, WORKSHOP

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Document> documents;

    @ManyToMany
    @JoinTable(
        name = "evenement_organisateurs",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private Set<Membre> organisateurs;
}