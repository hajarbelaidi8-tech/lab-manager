package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Partenaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String type; // ACADEMIQUE, INDUSTRIEL
    private String pays;
}