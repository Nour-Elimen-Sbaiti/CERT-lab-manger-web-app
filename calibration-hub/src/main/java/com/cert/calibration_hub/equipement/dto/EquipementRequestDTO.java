package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.PeriodiciteUnite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipementRequestDTO(
        @NotBlank String codeIdentifiant,
        String codeEmplacement,
        @NotBlank String designation,
        @NotBlank String marque,
        @NotBlank String modele,
        @NotBlank String numeroSerie,
        String fournisseur,
        @NotNull LocalDate dateAcquisition,
        LocalDate dateMiseEnService,
        BigDecimal valeurOrigine,
        @NotNull Integer periodiciteValeur,
        @NotNull PeriodiciteUnite periodiciteUnite,
        LocalDate garantieJusquAu,
        String observations
) {}