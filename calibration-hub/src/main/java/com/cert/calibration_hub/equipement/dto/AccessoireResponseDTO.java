package com.cert.calibration_hub.equipement.dto;

public record AccessoireResponseDTO(
        Long id,
        Long equipementId,
        String designation,
        String marque,
        String numeroSerie
) {}