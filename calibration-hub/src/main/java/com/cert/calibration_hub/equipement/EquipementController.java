package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.EquipementRequestDTO;
import com.cert.calibration_hub.equipement.dto.EquipementResponseDTO;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    private final EquipementService equipementService;
    private final FicheDeVieService ficheDeVieService;

    public EquipementController(EquipementService equipementService,
                                FicheDeVieService ficheDeVieService) {
        this.equipementService = equipementService;
        this.ficheDeVieService = ficheDeVieService;
    }

    @GetMapping
    public List<EquipementResponseDTO> findAll(
            @RequestParam(required = false) EtatEquipement etat,
            @RequestParam(required = false) String q) {
        if (etat != null) return equipementService.findByEtat(etat);
        if (q != null && !q.isBlank()) return equipementService.search(q);
        return equipementService.findAll();
    }

    @GetMapping("/{id}")
    public EquipementResponseDTO findById(@PathVariable Long id) {
        return equipementService.findById(id);
    }

    @GetMapping("/{id}/fiche-de-vie")
    public ResponseEntity<byte[]> telechargerFicheDeVie(@PathVariable Long id) {
        byte[] pdf = ficheDeVieService.genererPdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=fiche-de-vie-" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipementResponseDTO create(@Valid @RequestBody EquipementRequestDTO dto) {
        return equipementService.create(dto);
    }

    @PutMapping("/{id}")
    public EquipementResponseDTO update(@PathVariable Long id, @Valid @RequestBody EquipementRequestDTO dto) {
        return equipementService.update(id, dto);
    }

    @PatchMapping("/{id}/etat")
    public EquipementResponseDTO changerEtat(@PathVariable Long id, @RequestParam EtatEquipement etat) {
        return equipementService.changerEtat(id, etat);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reformer(@PathVariable Long id) {
        equipementService.reformer(id);
    }
    @PatchMapping("/{id}/personnel")
    public ResponseEntity<Void> assignerPersonnel(
            @PathVariable Long id,
            @RequestParam(required = false) Long responsableId,
            @RequestParam(required = false) Long suppleantId,
            @RequestParam(required = false) Long contactTechniqueId) {

        equipementService.assignerPersonnel(id, responsableId, suppleantId, contactTechniqueId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/labo-etalonnage")
    public ResponseEntity<Void> assignerLaboEtalonnage(@PathVariable Long id, @RequestParam Long laboId) {
        equipementService.assignerLaboEtalonnage(id, laboId);
        return ResponseEntity.noContent().build();
    }
}