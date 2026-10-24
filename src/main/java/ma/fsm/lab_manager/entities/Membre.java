package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity @Getter @Setter @ToString @EqualsAndHashCode @NoArgsConstructor @AllArgsConstructor
public class Membre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String password;
    private String photo; // Nom du fichier photo

    @Enumerated(EnumType.STRING)
    private StatutMembre statut; // permanent, associé, doctorant...

    @Enumerated(EnumType.STRING)
    private RoleMembre role;

    // CHAMPS PAGE 2 DU PDF
    private String etablissementOrigine;
    private String laboratoireOrigine;
    @Column(length = 2000)
    private String biographie;
    private String centresInteret;

    private LocalDate dateNaissance;
    private LocalDate dateEmbauche;
    private LocalDate dateAffiliation;

    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Equipe equipe;

    // ⭐ MATCH-I HAD L-BLOC (Line 41-49) ⭐
    @ManyToMany(mappedBy = "auteurs", fetch = FetchType.EAGER)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.Set<Publication> publications = new java.util.HashSet<>();

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Affiliation> affiliations = new java.util.ArrayList<>();
    
    // ⭐ MATCH SECTION 5.3 : L-KHIT M3A L-MANDATS ⭐
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Mandat> mandats = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "detenteur")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Materiel> materiels = new java.util.ArrayList<>();

    @ManyToMany(mappedBy = "organisateurs")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.Set<Evenement> evenementsOrganises = new java.util.HashSet<>();
}