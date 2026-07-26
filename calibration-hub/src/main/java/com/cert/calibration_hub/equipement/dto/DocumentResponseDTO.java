package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.TypeDocument;

import java.time.LocalDateTime;

public record DocumentResponseDTO(
        Long id,
        Long equipementId,
        TypeDocument typeDocument,
        String nomFichier,
        Long interventionId,
        Long contratMaintenanceId,
        LocalDateTime uploadedAt
) {}