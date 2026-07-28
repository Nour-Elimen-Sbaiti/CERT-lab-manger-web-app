package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "etalonnages")
public class Etalonnage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;

    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @Column(length = 100)
    private String emt;

    @Column(name = "bande_garde", length = 100)
    private String bandeGarde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultatEtalonnage resultat = ResultatEtalonnage.EN_ATTENTE;

    public Etalonnage() {
    }

    public boolean estExpire() {
        return dateValidite != null && dateValidite.isBefore(LocalDate.now());
    }

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public LocalDate getDateValidite() {
        return dateValidite;
    }

    public void setDateValidite(LocalDate dateValidite) {
        this.dateValidite = dateValidite;
    }

    public String getEmt() {
        return emt;
    }

    public void setEmt(String emt) {
        this.emt = emt;
    }

    public String getBandeGarde() {
        return bandeGarde;
    }

    public void setBandeGarde(String bandeGarde) {
        this.bandeGarde = bandeGarde;
    }

    public ResultatEtalonnage getResultat() {
        return resultat;
    }

    public void setResultat(ResultatEtalonnage resultat) {
        this.resultat = resultat;
    }
}