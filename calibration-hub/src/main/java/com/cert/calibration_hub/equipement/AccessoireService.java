package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AccessoireRequestDTO;
import com.cert.calibration_hub.equipement.dto.AccessoireResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessoireService {

    private final AccessoireRepository accessoireRepository;
    private final EquipementRepository equipementRepository;

    public AccessoireService(AccessoireRepository accessoireRepository,
                             EquipementRepository equipementRepository) {
        this.accessoireRepository = accessoireRepository;
        this.equipementRepository = equipementRepository;
    }

    public AccessoireResponseDTO creer(AccessoireRequestDTO dto) {
        Equipement equipement = equipementRepository.findById(dto.equipementId())
                .orElseThrow(() -> new EquipementNotFoundException(dto.equipementId()));

        Accessoire a = new Accessoire();
        a.setEquipement(equipement);
        a.setDesignation(dto.designation());
        a.setMarque(dto.marque());
        a.setNumeroSerie(dto.numeroSerie());

        return versDTO(accessoireRepository.save(a));
    }

    public List<AccessoireResponseDTO> listerParEquipement(Long equipementId) {
        return accessoireRepository.findByEquipementId(equipementId)
                .stream().map(this::versDTO).toList();
    }

    private AccessoireResponseDTO versDTO(Accessoire a) {
        return new AccessoireResponseDTO(
                a.getId(), a.getEquipement().getId(), a.getDesignation(),
                a.getMarque(), a.getNumeroSerie()
        );
    }
}