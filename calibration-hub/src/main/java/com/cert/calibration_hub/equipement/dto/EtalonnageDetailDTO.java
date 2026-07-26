package com.cert.calibration_hub.equipement.dto;

import java.time.LocalDate;

public record EtalonnageDetailDTO(
        LocalDate dateValidite,
        String emt,
        String bandeGarde
) {}