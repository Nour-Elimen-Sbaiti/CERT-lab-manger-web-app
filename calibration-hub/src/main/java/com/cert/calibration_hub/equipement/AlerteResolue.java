package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes_resolues")
public class AlerteResolue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alerte_key", nullable = false, unique = true, length = 160)
    private String alerteKey;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "equipement_id")
    private Long equipementId;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;

    @Column(name = "resolved_at", nullable = false)
    private LocalDateTime resolvedAt;

    public AlerteResolue() {
    }

    public AlerteResolue(String alerteKey, String type, Long sourceId, Long equipementId,
                         LocalDate dateEcheance, String observation, String resolvedBy) {
        this.alerteKey = alerteKey;
        this.type = type;
        this.sourceId = sourceId;
        this.equipementId = equipementId;
        this.dateEcheance = dateEcheance;
        this.observation = observation;
        this.resolvedBy = resolvedBy;
    }

    @PrePersist
    protected void onCreate() {
        if (this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getAlerteKey() {
        return alerteKey;
    }

    public void setAlerteKey(String alerteKey) {
        this.alerteKey = alerteKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(Long equipementId) {
        this.equipementId = equipementId;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
