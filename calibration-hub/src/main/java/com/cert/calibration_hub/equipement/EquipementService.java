package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.EquipementRequestDTO;
import com.cert.calibration_hub.equipement.dto.EquipementResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final PersonnelRepository personnelRepository;

    public EquipementService(EquipementRepository equipementRepository,
                             PersonnelRepository personnelRepository) {
        this.equipementRepository = equipementRepository;
        this.personnelRepository = personnelRepository;
    }

    public List<EquipementResponseDTO> findAll() {
        return equipementRepository.findAll().stream().map(this::toResponse).toList();
    }

    public EquipementResponseDTO findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public List<EquipementResponseDTO> findByEtat(EtatEquipement etat) {
        return equipementRepository.findByEtat(etat).stream().map(this::toResponse).toList();
    }

    public List<EquipementResponseDTO> search(String motCle) {
        return equipementRepository.findByDesignationContainingIgnoreCase(motCle)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public EquipementResponseDTO create(EquipementRequestDTO dto) {
        Equipement e = new Equipement();
        applyDto(e, dto);
        e.setEtat(EtatEquipement.NOUVEAU);
        e.setQrCode("EQUIP-" + dto.codeIdentifiant());
        return toResponse(equipementRepository.save(e));
    }

    @Transactional
    public EquipementResponseDTO update(Long id, EquipementRequestDTO dto) {
        Equipement e = getOrThrow(id);
        applyDto(e, dto);
        return toResponse(equipementRepository.save(e));
    }

    @Transactional
    public EquipementResponseDTO changerEtat(Long id, EtatEquipement nouvelEtat) {
        Equipement e = getOrThrow(id);
        e.setEtat(nouvelEtat);
        return toResponse(equipementRepository.save(e));
    }

    /** Jamais de suppression physique : l'équipement passe à l'état RÉFORMÉ. */
    @Transactional
    public void reformer(Long id) {
        Equipement e = getOrThrow(id);
        e.setEtat(EtatEquipement.REFORME);
        e.setDateReforme(LocalDate.now());
        equipementRepository.save(e);
    }

    @Transactional
    public void assignerPersonnel(Long equipementId, Long responsableId, Long suppleantId, Long contactTechniqueId) {
        Equipement e = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementNotFoundException(equipementId));

        if (responsableId != null) {
            Personnel responsable = personnelRepository.findById(responsableId)
                    .orElseThrow(() -> new RuntimeException("Personnel introuvable, id = " + responsableId));
            e.setResponsable(responsable);
        }
        if (suppleantId != null) {
            Personnel suppleant = personnelRepository.findById(suppleantId)
                    .orElseThrow(() -> new RuntimeException("Personnel introuvable, id = " + suppleantId));
            e.setSuppleant(suppleant);
        }
        if (contactTechniqueId != null) {
            Personnel contact = personnelRepository.findById(contactTechniqueId)
                    .orElseThrow(() -> new RuntimeException("Personnel introuvable, id = " + contactTechniqueId));
            e.setContactTechnique(contact);
        }

        equipementRepository.save(e);
    }

    private Equipement getOrThrow(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new EquipementNotFoundException(id));
    }

    private void applyDto(Equipement e, EquipementRequestDTO dto) {
        e.setCodeIdentifiant(dto.codeIdentifiant());
        e.setCodeEmplacement(dto.codeEmplacement());
        e.setDesignation(dto.designation());
        e.setMarque(dto.marque());
        e.setModele(dto.modele());
        e.setNumeroSerie(dto.numeroSerie());
        e.setFournisseur(dto.fournisseur());
        e.setDateAcquisition(dto.dateAcquisition());
        e.setDateMiseEnService(dto.dateMiseEnService());
        e.setValeurOrigine(dto.valeurOrigine());
        e.setPeriodiciteValeur(dto.periodiciteValeur());
        e.setPeriodiciteUnite(dto.periodiciteUnite());
        e.setGarantieJusquAu(dto.garantieJusquAu());
        e.setObservations(dto.observations());
    }

    private EquipementResponseDTO toResponse(Equipement e) {
        return new EquipementResponseDTO(
                e.getId(), e.getCodeIdentifiant(), e.getCodeEmplacement(), e.getQrCode(),
                e.getDesignation(), e.getMarque(), e.getModele(), e.getNumeroSerie(), e.getFournisseur(),
                e.getDateAcquisition(), e.getDateMiseEnService(), e.getDateReforme(), e.getValeurOrigine(),
                e.getPeriodiciteValeur(), e.getPeriodiciteUnite(), e.getGarantieJusquAu(),
                e.getEtat(), e.getObservations(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}