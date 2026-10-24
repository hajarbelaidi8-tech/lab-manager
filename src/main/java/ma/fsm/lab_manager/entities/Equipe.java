package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String acronyme; // Ex: "DSBD" pour Data Science & Big Data

    @Column(length = 1000)
    private String description;

    // Une équipe contient plusieurs membres
    @OneToMany(mappedBy = "equipe")
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private List<Membre> membres = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "equipe")
    @ToString.Exclude
    @Builder.Default
    private List<Publication> publications = new java.util.ArrayList<>();
}