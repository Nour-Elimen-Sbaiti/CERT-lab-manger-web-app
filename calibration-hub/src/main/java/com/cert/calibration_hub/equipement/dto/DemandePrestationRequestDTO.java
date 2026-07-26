package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.NiveauIncertitude;
import com.cert.calibration_hub.equipement.RegleDecision;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DemandePrestationRequestDTO(
        @NotNull Long etalonnageId,
        Long laboEtalonnageId,
        LocalDate dateDemande,
        String etatALaLivraison,
        String lieu,
        boolean comparaisonInterLabo,
        boolean declarationConformite,
        RegleDecision regleDecision,
        String pointsAEtalonner,
        NiveauIncertitude niveauIncertitude
) {}