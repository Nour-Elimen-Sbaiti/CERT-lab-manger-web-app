package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.EtalonnageDetailDTO;
import com.cert.calibration_hub.equipement.dto.InterventionRequestDTO;
import com.cert.calibration_hub.equipement.dto.InterventionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private final EquipementRepository equipementRepository;
    private final EtalonnageRepository etalonnageRepository;

    public InterventionService(InterventionRepository interventionRepository,
                               EquipementRepository equipementRepository,
                               EtalonnageRepository etalonnageRepository) {
        this.interventionRepository = interventionRepository;
        this.equipementRepository = equipementRepository;
        this.etalonnageRepository = etalonnageRepository;
    }

    @Transactional
    public InterventionResponseDTO creer(InterventionRequestDTO dto) {

        // Règle métier : cohérence type <-> détail étalonnage
        if (dto.type() == TypeIntervention.ETALONNAGE && dto.etalonnage() == null) {
            throw new InterventionInvalideException(
                    "Le détail d'étalonnage (dateValidite, emt, bandeGarde) est obligatoire pour une intervention de type ETALONNAGE.");
        }

        Equipement equipement = equipementRepository.findById(dto.equipementId())
                .orElseThrow(() -> new EquipementNotFoundException(dto.equipementId()));



        Intervention intervention = new Intervention();
        intervention.setEquipement(equipement);
        intervention.setType(dto.type());
        intervention.setDateIntervention(dto.dateIntervention());
        intervention.setNatureIntervention(dto.natureIntervention());
        intervention.setCout(dto.cout());
        intervention.setNumeroBonIntervention(dto.numeroBonIntervention());
        intervention.setIntervenantNom(dto.intervenantNom());
        intervention.setIntervenantSociete(dto.intervenantSociete());

        Intervention interventionSauvegardee = interventionRepository.save(intervention);

        Etalonnage etalonnageSauvegarde = null;
        if (dto.type() == TypeIntervention.ETALONNAGE) {
            EtalonnageDetailDTO detail = dto.etalonnage();
            Etalonnage etalonnage = new Etalonnage();
            etalonnage.setIntervention(interventionSauvegardee);
            etalonnage.setDateValidite(detail.dateValidite());
            etalonnage.setEmt(detail.emt());
            etalonnage.setBandeGarde(detail.bandeGarde());
            etalonnageSauvegarde = etalonnageRepository.save(etalonnage);
        }

        return versDTO(interventionSauvegardee, etalonnageSauvegarde);
    }

    public List<InterventionResponseDTO> listerParEquipement(Long equipementId) {
        return interventionRepository.findByEquipementIdOrderByDateInterventionDesc(equipementId)
                .stream()
                .map(i -> versDTO(i, etalonnageRepository.findByInterventionId(i.getId()).orElse(null)))
                .toList();
    }

    private InterventionResponseDTO versDTO(Intervention i, Etalonnage e) {
        EtalonnageDetailDTO etalonnageDto = (e == null) ? null :
                new EtalonnageDetailDTO(e.getDateValidite(), e.getEmt(), e.getBandeGarde());

        return new InterventionResponseDTO(
                i.getId(),
                i.getEquipement().getId(),
                i.getType(),
                i.getDateIntervention(),
                i.getNatureIntervention(),
                i.getCout(),
                i.getNumeroBonIntervention(),
                i.getIntervenantNom(),
                i.getIntervenantSociete(),
                i.getLaboEtalonnage() != null ? i.getLaboEtalonnage().getNom() : null,
                i.getStatut(),
                etalonnageDto,
                i.getCreatedAt()
        );
    }
}