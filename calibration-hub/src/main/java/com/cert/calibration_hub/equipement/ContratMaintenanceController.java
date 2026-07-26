package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.ContratMaintenanceRequestDTO;
import com.cert.calibration_hub.equipement.dto.ContratMaintenanceResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats-maintenance")
public class ContratMaintenanceController {

    private final ContratMaintenanceService contratMaintenanceService;

    public ContratMaintenanceController(ContratMaintenanceService contratMaintenanceService) {
        this.contratMaintenanceService = contratMaintenanceService;
    }

    @PostMapping
    public ResponseEntity<ContratMaintenanceResponseDTO> creer(@Valid @RequestBody ContratMaintenanceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratMaintenanceService.creer(dto));
    }

    @GetMapping("/equipement/{equipementId}")
    public ResponseEntity<List<ContratMaintenanceResponseDTO>> listerParEquipement(@PathVariable Long equipementId) {
        return ResponseEntity.ok(contratMaintenanceService.listerParEquipement(equipementId));
    }
}