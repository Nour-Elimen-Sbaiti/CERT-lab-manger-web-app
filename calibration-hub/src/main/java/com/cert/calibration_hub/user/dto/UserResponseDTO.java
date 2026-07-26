package com.cert.calibration_hub.user.dto;

import com.cert.calibration_hub.user.Role;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String nom,
        String prenom,
        String email,
        Role role,
        boolean enabled,
        boolean emailVerified,
        LocalDateTime createdAt
) {}