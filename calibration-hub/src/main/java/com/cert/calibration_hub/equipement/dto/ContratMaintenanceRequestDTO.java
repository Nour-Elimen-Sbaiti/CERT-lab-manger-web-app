package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.PeriodiciteUnite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContratMaintenanceRequestDTO(
        @NotNull Long equipementId,
        @NotBlank String prestataire,
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin,
        @NotNull Integer periodeValeur,
        @NotNull PeriodiciteUnite periodeUnite
) {}