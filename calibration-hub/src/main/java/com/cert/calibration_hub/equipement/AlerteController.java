package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AlerteDTO;
import com.cert.calibration_hub.equipement.dto.AlerteResolutionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class AlerteController {

    private final AlerteService alerteService;

    public AlerteController(AlerteService alerteService) {
        this.alerteService = alerteService;
    }

    @GetMapping("/api/alertes")
    public List<AlerteDTO> obtenirAlertes(
            @RequestParam(defaultValue = "30") int jours,
            @RequestParam(defaultValue = "false") boolean inclureResolues,
            @RequestParam(required = false) Boolean includeResolved) {
        boolean afficherResolues = inclureResolues || Boolean.TRUE.equals(includeResolved);
        return alerteService.obtenirAlertes(jours, afficherResolues);
    }

    @PatchMapping({
            "/api/alertes/{alerteId}/resoudre",
            "/api/alertes/{alerteId}/resolu",
            "/api/alertes/{alerteId}/resolue",
            "/api/alertes/{alerteId}/resolve",
            "/api/alertes/{alerteId}/resolved"
    })
    public ResponseEntity<AlerteDTO> resoudreAlerte(@PathVariable String alerteId,
                                                    @Valid @RequestBody(required = false) AlerteResolutionRequestDTO request,
                                                    Principal principal) {
        String resolvedBy = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(alerteService.resoudreAlerte(alerteId, request, resolvedBy));
    }

    @DeleteMapping({
            "/api/alertes/{alerteId}/resolution",
            "/api/alertes/{alerteId}/resolu",
            "/api/alertes/{alerteId}/resolue",
            "/api/alertes/{alerteId}/resolve",
            "/api/alertes/{alerteId}/resolved"
    })
    public ResponseEntity<Void> rouvrirAlerte(@PathVariable String alerteId) {
        alerteService.rouvrirAlerte(alerteId);
        return ResponseEntity.noContent().build();
    }
}
