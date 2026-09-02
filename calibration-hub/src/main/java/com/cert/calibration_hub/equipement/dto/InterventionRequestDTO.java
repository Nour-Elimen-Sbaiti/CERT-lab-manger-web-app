package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.TypeIntervention;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InterventionRequestDTO(
        @NotNull Long equipementId,
        @NotNull TypeIntervention type,
        @NotNull LocalDate dateIntervention,
        @NotBlank String natureIntervention,
        BigDecimal cout,
        String numeroBonIntervention,
        String intervenantNom,
        String intervenantSociete,
        Long laboEtalonnageId,
        @Valid EtalonnageDetailDTO etalonnage  // rempli seulement si type = ETALONNAGE, sinon null
) {}