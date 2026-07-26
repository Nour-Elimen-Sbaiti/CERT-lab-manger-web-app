package com.cert.calibration_hub.equipement.dto;

public record LaboEtalonnageResponseDTO(
        Long id,
        String nom,
        String contactNom,
        String contactPrenom,
        String contactEmail,
        String contactTelephone
) {}