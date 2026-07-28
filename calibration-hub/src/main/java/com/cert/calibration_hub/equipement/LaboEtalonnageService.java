package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.LaboEtalonnageRequestDTO;
import com.cert.calibration_hub.equipement.dto.LaboEtalonnageResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboEtalonnageService {

    private final LaboEtalonnageRepository laboEtalonnageRepository;

    public LaboEtalonnageService(LaboEtalonnageRepository laboEtalonnageRepository) {
        this.laboEtalonnageRepository = laboEtalonnageRepository;
    }

    public LaboEtalonnageResponseDTO creer(LaboEtalonnageRequestDTO dto) {
        LaboEtalonnage labo = new LaboEtalonnage();
        appliquerDto(labo, dto);
        return versDTO(laboEtalonnageRepository.save(labo));
    }

    public List<LaboEtalonnageResponseDTO> lister() {
        return laboEtalonnageRepository.findAll().stream().map(this::versDTO).toList();
    }

    public LaboEtalonnageResponseDTO trouverParId(Long id) {
        return versDTO(getOrThrow(id));
    }

    public LaboEtalonnageResponseDTO modifier(Long id, LaboEtalonnageRequestDTO dto) {
        LaboEtalonnage labo = getOrThrow(id);
        appliquerDto(labo, dto);
        return versDTO(laboEtalonnageRepository.save(labo));
    }

    public void supprimer(Long id) {
        LaboEtalonnage labo = getOrThrow(id);
        laboEtalonnageRepository.delete(labo);
    }


    private LaboEtalonnage getOrThrow(Long id) {
        return laboEtalonnageRepository.findById(id)
                .orElseThrow(() -> new LaboEtalonnageNotFoundException(id));
    }

    private void appliquerDto(LaboEtalonnage labo, LaboEtalonnageRequestDTO dto) {
        labo.setNom(dto.nom());
        labo.setContactNom(dto.contactNom());
        labo.setContactPrenom(dto.contactPrenom());
        labo.setContactEmail(dto.contactEmail());
        labo.setContactTelephone(dto.contactTelephone());
    }

    private LaboEtalonnageResponseDTO versDTO(LaboEtalonnage labo) {
        return new LaboEtalonnageResponseDTO(
                labo.getId(), labo.getNom(), labo.getContactNom(),
                labo.getContactPrenom(), labo.getContactEmail(), labo.getContactTelephone()
        );
    }

    // LaboEtalonnageService.java
}