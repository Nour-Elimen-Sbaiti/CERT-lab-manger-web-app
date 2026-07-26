package com.cert.calibration_hub.equipement.dto;

import java.util.Map;

public record DashboardDTO(
        long totalEquipements,
        Map<String, Long> repartitionParEtat,
        long totalPersonnel,
        long totalAccessoires,
        long nombreAlertesActives
) {}