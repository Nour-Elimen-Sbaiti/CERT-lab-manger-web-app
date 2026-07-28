package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;

@Entity
@Table(name = "accessoires")
public class Accessoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(length = 100)
    private String marque;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    public Accessoire() {
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
}