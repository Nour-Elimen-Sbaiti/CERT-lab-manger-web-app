package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipements")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_identifiant", nullable = false, unique = true, length = 50)
    private String codeIdentifiant;

    @Column(name = "code_emplacement", length = 20)
    private String codeEmplacement;

    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode;

    @Column(nullable = false, length = 150)
    private String designation;

    @Column(nullable = false, length = 100)
    private String marque;

    @Column(nullable = false, length = 100)
    private String modele;

    @Column(name = "numero_serie", nullable = false, unique = true, length = 100)
    private String numeroSerie;

    @Column(length = 150)
    private String fournisseur;

    @Column(name = "date_acquisition", nullable = false)
    private LocalDate dateAcquisition;

    @Column(name = "date_mise_en_service")
    private LocalDate dateMiseEnService;

    @Column(name = "date_reforme")
    private LocalDate dateReforme;

    @Column(name = "valeur_origine", precision = 12, scale = 3)
    private BigDecimal valeurOrigine;

    @Column(name = "periodicite_valeur", nullable = false)
    private Integer periodiciteValeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicite_unite", nullable = false, length = 10)
    private PeriodiciteUnite periodiciteUnite = PeriodiciteUnite.ANNEE;

    @Column(name = "garantie_jusqu_au")
    private LocalDate garantieJusquAu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EtatEquipement etat = EtatEquipement.NOUVEAU;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Personnel responsable;

    @ManyToOne
    @JoinColumn(name = "suppleant_id")
    private Personnel suppleant;

    @ManyToOne
    @JoinColumn(name = "contact_technique_id")
    private Personnel contactTechnique;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Constructeurs ---

    public Equipement() {
    }

    // --- Callbacks JPA ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public String getCodeIdentifiant() {
        return codeIdentifiant;
    }

    public void setCodeIdentifiant(String codeIdentifiant) {
        this.codeIdentifiant = codeIdentifiant;
    }

    public String getCodeEmplacement() {
        return codeEmplacement;
    }

    public void setCodeEmplacement(String codeEmplacement) {
        this.codeEmplacement = codeEmplacement;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public LocalDate getDateAcquisition() {
        return dateAcquisition;
    }

    public void setDateAcquisition(LocalDate dateAcquisition) {
        this.dateAcquisition = dateAcquisition;
    }

    public LocalDate getDateMiseEnService() {
        return dateMiseEnService;
    }

    public void setDateMiseEnService(LocalDate dateMiseEnService) {
        this.dateMiseEnService = dateMiseEnService;
    }

    public LocalDate getDateReforme() {
        return dateReforme;
    }

    public void setDateReforme(LocalDate dateReforme) {
        this.dateReforme = dateReforme;
    }

    public BigDecimal getValeurOrigine() {
        return valeurOrigine;
    }

    public void setValeurOrigine(BigDecimal valeurOrigine) {
        this.valeurOrigine = valeurOrigine;
    }

    public Integer getPeriodiciteValeur() {
        return periodiciteValeur;
    }

    public void setPeriodiciteValeur(Integer periodiciteValeur) {
        this.periodiciteValeur = periodiciteValeur;
    }

    public PeriodiciteUnite getPeriodiciteUnite() {
        return periodiciteUnite;
    }

    public void setPeriodiciteUnite(PeriodiciteUnite periodiciteUnite) {
        this.periodiciteUnite = periodiciteUnite;
    }

    public LocalDate getGarantieJusquAu() {
        return garantieJusquAu;
    }

    public void setGarantieJusquAu(LocalDate garantieJusquAu) {
        this.garantieJusquAu = garantieJusquAu;
    }

    public EtatEquipement getEtat() {
        return etat;
    }

    public void setEtat(EtatEquipement etat) {
        this.etat = etat;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Personnel getResponsable() {
        return responsable;
    }

    public void setResponsable(Personnel responsable) {
        this.responsable = responsable;
    }

    public Personnel getSuppleant() {
        return suppleant;
    }

    public void setSuppleant(Personnel suppleant) {
        this.suppleant = suppleant;
    }

    public Personnel getContactTechnique() {
        return contactTechnique;
    }

    public void setContactTechnique(Personnel contactTechnique) {
        this.contactTechnique = contactTechnique;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}