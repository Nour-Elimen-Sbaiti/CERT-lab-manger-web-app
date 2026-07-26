package com.cert.calibration_hub.equipement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccessoireRequestDTO(
        @NotNull Long equipementId,
        @NotBlank String designation,
        String marque,
        String numeroSerie
) {}