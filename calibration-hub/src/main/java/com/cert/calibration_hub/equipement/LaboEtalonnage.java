package com.cert.calibration_hub.equipement;

import jakarta.persistence.*;

@Entity
@Table(name = "labos_etalonnage")
public class LaboEtalonnage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(name = "contact_nom", length = 100)
    private String contactNom;

    @Column(name = "contact_prenom", length = 100)
    private String contactPrenom;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(name = "contact_telephone", length = 30)
    private String contactTelephone;

    public LaboEtalonnage() {
    }

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContactNom() {
        return contactNom;
    }

    public void setContactNom(String contactNom) {
        this.contactNom = contactNom;
    }

    public String getContactPrenom() {
        return contactPrenom;
    }

    public void setContactPrenom(String contactPrenom) {
        this.contactPrenom = contactPrenom;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }
}