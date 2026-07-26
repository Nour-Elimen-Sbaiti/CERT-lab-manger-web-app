package com.cert.calibration_hub.equipement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LaboEtalonnageRequestDTO(
        @NotBlank String nom,
        String contactNom,
        String contactPrenom,
        @Email String contactEmail,
        String contactTelephone
) {}