package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.EtatEquipement;
import com.cert.calibration_hub.equipement.PeriodiciteUnite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EquipementResponseDTO(
        Long id,
        String codeIdentifiant,
        String codeEmplacement,
        String qrCode,
        String designation,
        String marque,
        String modele,
        String numeroSerie,
        String fournisseur,
        LocalDate dateAcquisition,
        LocalDate dateMiseEnService,
        LocalDate dateReforme,
        BigDecimal valeurOrigine,
        Integer periodiciteValeur,
        PeriodiciteUnite periodiciteUnite,
        LocalDate garantieJusquAu,
        EtatEquipement etat,
        String laboEtalonnageNom,
        String observations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}