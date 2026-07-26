package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DemandePrestationRequestDTO;
import com.cert.calibration_hub.equipement.dto.DemandePrestationResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-prestation")
public class DemandePrestationController {

    private final DemandePrestationService demandePrestationService;
    private final DemandePrestationPdfService demandePrestationPdfService;

    public DemandePrestationController(DemandePrestationService demandePrestationService,
                                       DemandePrestationPdfService demandePrestationPdfService) {
        this.demandePrestationService = demandePrestationService;
        this.demandePrestationPdfService = demandePrestationPdfService;
    }

    @PostMapping
    public ResponseEntity<DemandePrestationResponseDTO> creer(@Valid @RequestBody DemandePrestationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(demandePrestationService.creer(dto));
    }

    @GetMapping
    public ResponseEntity<List<DemandePrestationResponseDTO>> lister() {
        return ResponseEntity.ok(demandePrestationService.lister());
    }

    @GetMapping("/etalonnage/{etalonnageId}")
    public ResponseEntity<DemandePrestationResponseDTO> trouverParEtalonnage(@PathVariable Long etalonnageId) {
        return ResponseEntity.ok(demandePrestationService.trouverParEtalonnage(etalonnageId));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> telechargerPdf(@PathVariable Long id) {
        byte[] pdf = demandePrestationPdfService.genererPdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=demande-prestation-" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}