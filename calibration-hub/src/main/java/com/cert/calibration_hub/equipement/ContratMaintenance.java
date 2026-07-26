package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrats_maintenance")
public class ContratMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(nullable = false, length = 150)
    private String prestataire;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "periode_valeur", nullable = false)
    private Integer periodeValeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "periode_unite", nullable = false, length = 10)
    private PeriodiciteUnite periodeUnite;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ContratMaintenance() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean estValide() {
        return dateFin != null && !dateFin.isBefore(LocalDate.now());
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

    public String getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(String prestataire) {
        this.prestataire = prestataire;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getPeriodeValeur() {
        return periodeValeur;
    }

    public void setPeriodeValeur(Integer periodeValeur) {
        this.periodeValeur = periodeValeur;
    }

    public PeriodiciteUnite getPeriodeUnite() {
        return periodeUnite;
    }

    public void setPeriodeUnite(PeriodiciteUnite periodeUnite) {
        this.periodeUnite = periodeUnite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}