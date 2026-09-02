package com.cert.calibration_hub.equipement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlerteDTO(
        String id,
        String type,
        Long sourceId,
        Long equipementId,
        String equipementDesignation,
        String equipementCodeIdentifiant,
        LocalDate dateEcheance,
        long joursRestants,
        String details,
        boolean resolue,
        String observationResolution,
        String resolvedBy,
        LocalDateTime resolvedAt
) {}
