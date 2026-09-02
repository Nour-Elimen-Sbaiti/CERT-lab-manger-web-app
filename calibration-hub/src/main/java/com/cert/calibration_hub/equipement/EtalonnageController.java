package com.cert.calibration_hub.equipement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/etalonnages")
public class EtalonnageController {

    private final EtalonnageRepository etalonnageRepository;

    public EtalonnageController(EtalonnageRepository etalonnageRepository) {
        this.etalonnageRepository = etalonnageRepository;
    }

    public record EtalonnageListDTO(
            Long id,
            Long equipementId,
            String equipementDesignation,
            String equipementCodeIdentifiant,
            String dateValidite,
            String resultat
    ) {}

    @GetMapping
    public ResponseEntity<List<EtalonnageListDTO>> lister() {
        List<EtalonnageListDTO> dtos = etalonnageRepository.findAll().stream()
                .map(e -> new EtalonnageListDTO(
                        e.getId(),
                        e.getIntervention().getEquipement().getId(),
                        e.getIntervention().getEquipement().getDesignation(),
                        e.getIntervention().getEquipement().getCodeIdentifiant(),
                        e.getDateValidite() != null ? e.getDateValidite().toString() : null,
                        e.getResultat().name()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
