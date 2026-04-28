package com.autiguide.autiguide.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "enfants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prenom;

    private LocalDate dateNaissance;

    private String sexe;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "enfant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resultat> resultats;

    @OneToMany(mappedBy = "enfant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SuiviJournalier> suivis;

    public int calculerAge() {
        return Period.between(this.dateNaissance, LocalDate.now()).getYears();
    }
}