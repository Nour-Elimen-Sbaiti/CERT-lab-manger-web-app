package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.LaboEtalonnageRequestDTO;
import com.cert.calibration_hub.equipement.dto.LaboEtalonnageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labos-etalonnage")
public class LaboEtalonnageController {

    private final LaboEtalonnageService laboEtalonnageService;

    public LaboEtalonnageController(LaboEtalonnageService laboEtalonnageService) {
        this.laboEtalonnageService = laboEtalonnageService;
    }

    @PostMapping
    public ResponseEntity<LaboEtalonnageResponseDTO> creer(@Valid @RequestBody LaboEtalonnageRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laboEtalonnageService.creer(dto));
    }

    @GetMapping
    public ResponseEntity<List<LaboEtalonnageResponseDTO>> lister() {
        return ResponseEntity.ok(laboEtalonnageService.lister());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboEtalonnageResponseDTO> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(laboEtalonnageService.trouverParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboEtalonnageResponseDTO> modifier(@PathVariable Long id,
                                                              @Valid @RequestBody LaboEtalonnageRequestDTO dto) {
        return ResponseEntity.ok(laboEtalonnageService.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        laboEtalonnageService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}