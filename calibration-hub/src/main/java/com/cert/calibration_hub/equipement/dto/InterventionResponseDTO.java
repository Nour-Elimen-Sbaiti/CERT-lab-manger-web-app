package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.StatutIntervention;
import com.cert.calibration_hub.equipement.TypeIntervention;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InterventionResponseDTO(
        Long id,
        Long equipementId,
        TypeIntervention type,
        LocalDate dateIntervention,
        String natureIntervention,
        BigDecimal cout,
        String numeroBonIntervention,
        String intervenantNom,
        String intervenantSociete,
        StatutIntervention statut,
        EtalonnageDetailDTO etalonnage,
        LocalDateTime createdAt
) {}