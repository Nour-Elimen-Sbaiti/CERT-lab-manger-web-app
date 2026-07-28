package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_prestation")
public class DemandePrestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "etalonnage_id", nullable = false, unique = true)
    private Etalonnage etalonnage;

    @ManyToOne
    @JoinColumn(name = "labo_etalonnage_id")
    private LaboEtalonnage laboEtalonnage;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Column(name = "etat_a_la_livraison", columnDefinition = "TEXT")
    private String etatALaLivraison;

    @Column(length = 20)
    private String lieu;

    @Column(name = "comparaison_inter_labo", nullable = false)
    private boolean comparaisonInterLabo = false;

    @Column(name = "declaration_conformite", nullable = false)
    private boolean declarationConformite = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "regle_decision", length = 40)
    private RegleDecision regleDecision;

    @Column(name = "points_a_etalonner", columnDefinition = "TEXT")
    private String pointsAEtalonner;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_incertitude", length = 15)
    private NiveauIncertitude niveauIncertitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DemandePrestation() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.dateDemande == null) {
            this.dateDemande = LocalDate.now();
        }
    }

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public Etalonnage getEtalonnage() {
        return etalonnage;
    }

    public void setEtalonnage(Etalonnage etalonnage) {
        this.etalonnage = etalonnage;
    }

    public LaboEtalonnage getLaboEtalonnage() {
        return laboEtalonnage;
    }

    public void setLaboEtalonnage(LaboEtalonnage laboEtalonnage) {
        this.laboEtalonnage = laboEtalonnage;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getEtatALaLivraison() {
        return etatALaLivraison;
    }

    public void setEtatALaLivraison(String etatALaLivraison) {
        this.etatALaLivraison = etatALaLivraison;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public boolean isComparaisonInterLabo() {
        return comparaisonInterLabo;
    }

    public void setComparaisonInterLabo(boolean comparaisonInterLabo) {
        this.comparaisonInterLabo = comparaisonInterLabo;
    }

    public boolean isDeclarationConformite() {
        return declarationConformite;
    }

    public void setDeclarationConformite(boolean declarationConformite) {
        this.declarationConformite = declarationConformite;
    }

    public RegleDecision getRegleDecision() {
        return regleDecision;
    }

    public void setRegleDecision(RegleDecision regleDecision) {
        this.regleDecision = regleDecision;
    }

    public String getPointsAEtalonner() {
        return pointsAEtalonner;
    }

    public void setPointsAEtalonner(String pointsAEtalonner) {
        this.pointsAEtalonner = pointsAEtalonner;
    }

    public NiveauIncertitude getNiveauIncertitude() {
        return niveauIncertitude;
    }

    public void setNiveauIncertitude(NiveauIncertitude niveauIncertitude) {
        this.niveauIncertitude = niveauIncertitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}