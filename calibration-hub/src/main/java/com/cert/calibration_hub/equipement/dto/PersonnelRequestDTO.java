package com.cert.calibration_hub.equipement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonnelRequestDTO(
        @NotBlank String nom,
        String prenom,
        @Email String email,
        String telephone
) {}