package com.cert.calibration_hub.equipement.dto;

import java.time.LocalDate;

public record AlerteDTO(
        String type,
        Long equipementId,
        String equipementDesignation,
        String equipementCodeIdentifiant,
        LocalDate dateEcheance,
        long joursRestants,
        String details
) {}