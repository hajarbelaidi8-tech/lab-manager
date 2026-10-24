package ma.fsm.lab_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String contenu;

    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @ManyToOne
    private Membre expediteur;

    @ManyToOne
    private Equipe equipe; // Si null, le message est pour tout le labo

    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonBackReference
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Evenement evenement; // Si non null, discussion liée à un événement
}