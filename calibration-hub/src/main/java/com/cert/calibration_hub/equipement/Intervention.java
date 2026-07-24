package com.cert.calibration_hub.equipement ;

import com.cert.calibration_hub.equipement.Equipement;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeIntervention type;

    @Column(name = "date_intervention", nullable = false)
    private LocalDate dateIntervention;

    @Column(name = "nature_intervention", columnDefinition = "TEXT", nullable = false)
    private String natureIntervention;

    @Column(precision = 12, scale = 3)
    private BigDecimal cout;

    @Column(name = "numero_bon_intervention", length = 50)
    private String numeroBonIntervention;

    @Column(name = "intervenant_nom", length = 150)
    private String intervenantNom;

    @Column(name = "intervenant_societe", length = 150)
    private String intervenantSociete;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Intervention() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public TypeIntervention getType() {
        return type;
    }

    public void setType(TypeIntervention type) {
        this.type = type;
    }

    public LocalDate getDateIntervention() {
        return dateIntervention;
    }

    public void setDateIntervention(LocalDate dateIntervention) {
        this.dateIntervention = dateIntervention;
    }

    public String getNatureIntervention() {
        return natureIntervention;
    }

    public void setNatureIntervention(String natureIntervention) {
        this.natureIntervention = natureIntervention;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public String getNumeroBonIntervention() {
        return numeroBonIntervention;
    }

    public void setNumeroBonIntervention(String numeroBonIntervention) {
        this.numeroBonIntervention = numeroBonIntervention;
    }

    public String getIntervenantNom() {
        return intervenantNom;
    }

    public void setIntervenantNom(String intervenantNom) {
        this.intervenantNom = intervenantNom;
    }

    public String getIntervenantSociete() {
        return intervenantSociete;
    }

    public void setIntervenantSociete(String intervenantSociete) {
        this.intervenantSociete = intervenantSociete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}