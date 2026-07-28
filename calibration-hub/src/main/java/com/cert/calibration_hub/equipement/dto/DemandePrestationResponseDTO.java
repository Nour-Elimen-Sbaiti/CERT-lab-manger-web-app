package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.NiveauIncertitude;
import com.cert.calibration_hub.equipement.RegleDecision;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DemandePrestationResponseDTO(
        Long id,
        Long etalonnageId,
        Long equipementId,
        String laboEtalonnageNom,
        LocalDate dateDemande,
        String etatALaLivraison,
        String lieu,
        boolean comparaisonInterLabo,
        boolean declarationConformite,
        RegleDecision regleDecision,
        String pointsAEtalonner,
        NiveauIncertitude niveauIncertitude,
        LocalDateTime createdAt
) {}