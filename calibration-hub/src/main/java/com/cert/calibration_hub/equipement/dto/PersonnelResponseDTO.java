package com.cert.calibration_hub.equipement.dto;

public record PersonnelResponseDTO(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone
) {}