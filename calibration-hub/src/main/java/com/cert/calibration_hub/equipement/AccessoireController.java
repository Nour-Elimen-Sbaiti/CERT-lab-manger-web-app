package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AccessoireRequestDTO;
import com.cert.calibration_hub.equipement.dto.AccessoireResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessoires")
public class AccessoireController {

    private final AccessoireService accessoireService;

    public AccessoireController(AccessoireService accessoireService) {
        this.accessoireService = accessoireService;
    }

    @PostMapping
    public ResponseEntity<AccessoireResponseDTO> creer(@Valid @RequestBody AccessoireRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accessoireService.creer(dto));
    }

    @GetMapping("/equipement/{equipementId}")
    public ResponseEntity<List<AccessoireResponseDTO>> listerParEquipement(@PathVariable Long equipementId) {
        return ResponseEntity.ok(accessoireService.listerParEquipement(equipementId));
    }
}