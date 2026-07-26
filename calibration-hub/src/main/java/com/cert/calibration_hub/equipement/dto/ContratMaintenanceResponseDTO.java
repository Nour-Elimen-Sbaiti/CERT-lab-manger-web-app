package com.cert.calibration_hub.equipement.dto;

import com.cert.calibration_hub.equipement.PeriodiciteUnite;

import java.time.LocalDate;

public record ContratMaintenanceResponseDTO(
        Long id,
        Long equipementId,
        String prestataire,
        LocalDate dateDebut,
        LocalDate dateFin,
        Integer periodeValeur,
        PeriodiciteUnite periodeUnite,
        boolean actif
) {}