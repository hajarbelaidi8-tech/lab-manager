package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity @Data @NoArgsConstructor @AllArgsConstructor

public class Document {
    // ... id, titre, cheminFichier ...
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Column(name="chemin_fichier")
    private String cheminFichier; // Exemple: /uploads/pv_reunion.pdf
    // ⭐ MATCH SECTION 10 : HISTORISATION PAR DATE ⭐
    private java.time.LocalDateTime dateUpload;

    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument; // ⭐ ST3MEL L-ENUM HNA ⭐

    @ManyToOne
    private Evenement evenement;
}


