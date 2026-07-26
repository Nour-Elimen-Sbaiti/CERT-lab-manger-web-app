package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.InterventionRequestDTO;
import com.cert.calibration_hub.equipement.dto.InterventionResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @PostMapping
    public ResponseEntity<InterventionResponseDTO> creer(@Valid @RequestBody InterventionRequestDTO dto) {
        InterventionResponseDTO reponse = interventionService.creer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping("/equipement/{equipementId}")
    public ResponseEntity<List<InterventionResponseDTO>> listerParEquipement(@PathVariable Long equipementId) {
        return ResponseEntity.ok(interventionService.listerParEquipement(equipementId));
    }
}