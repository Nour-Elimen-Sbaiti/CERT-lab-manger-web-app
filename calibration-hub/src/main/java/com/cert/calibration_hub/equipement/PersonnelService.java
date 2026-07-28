package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.PersonnelRequestDTO;
import com.cert.calibration_hub.equipement.dto.PersonnelResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

    public PersonnelResponseDTO creer(PersonnelRequestDTO dto) {
        Personnel p = new Personnel();
        p.setNom(dto.nom());
        p.setPrenom(dto.prenom());
        p.setEmail(dto.email());
        p.setTelephone(dto.telephone());

        return versDTO(personnelRepository.save(p));
    }

    public List<PersonnelResponseDTO> lister() {
        return personnelRepository.findAll().stream().map(this::versDTO).toList();
    }

    // PersonnelService.java
    public void delete(Long id) {
        if (!personnelRepository.existsById(id)) {
            throw new RuntimeException("Personnel introuvable, id = " + id);
        }
        personnelRepository.deleteById(id);
    }

    private PersonnelResponseDTO versDTO(Personnel p) {
        return new PersonnelResponseDTO(p.getId(), p.getNom(), p.getPrenom(), p.getEmail(), p.getTelephone());
    }
}

