package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AlerteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlerteController {

    private final AlerteService alerteService;

    public AlerteController(AlerteService alerteService) {
        this.alerteService = alerteService;
    }

    @GetMapping("/api/alertes")
    public List<AlerteDTO> obtenirAlertes(
            @RequestParam(defaultValue = "30") int jours) {
        return alerteService.obtenirAlertes(jours);
    }
}