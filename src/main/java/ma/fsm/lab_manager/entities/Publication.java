package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Publication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String revue; // Nom du journal ou de la conférence
    private LocalDate datePublication;
    private String doi;
    @Column(name="chemin_fichier")
    private String cheminFichier;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "membre_publications",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private java.util.Set<Membre> auteurs = new java.util.HashSet<>();
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
}