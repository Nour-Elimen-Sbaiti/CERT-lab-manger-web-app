package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false, length = 30)
    private TypeDocument typeDocument;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "chemin_stockage", nullable = false, length = 500)
    private String cheminStockage;

    // Rempli seulement si type = CERTIFICAT_ETALONNAGE
    @ManyToOne
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    // Rempli seulement si type = CONTRAT_MAINTENANCE
    @ManyToOne
    @JoinColumn(name = "contrat_maintenance_id")
    private ContratMaintenance contratMaintenance;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public Document() {
    }

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
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

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminStockage() {
        return cheminStockage;
    }

    public void setCheminStockage(String cheminStockage) {
        this.cheminStockage = cheminStockage;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public ContratMaintenance getContratMaintenance() {
        return contratMaintenance;
    }

    public void setContratMaintenance(ContratMaintenance contratMaintenance) {
        this.contratMaintenance = contratMaintenance;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}