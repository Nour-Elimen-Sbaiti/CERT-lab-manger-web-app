package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DemandePrestationRequestDTO;
import com.cert.calibration_hub.equipement.dto.DemandePrestationResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandePrestationService {

    private final DemandePrestationRepository demandePrestationRepository;
    private final EtalonnageRepository etalonnageRepository;
    private final LaboEtalonnageRepository laboEtalonnageRepository;

    public DemandePrestationService(DemandePrestationRepository demandePrestationRepository,
                                    EtalonnageRepository etalonnageRepository,
                                    LaboEtalonnageRepository laboEtalonnageRepository) {
        this.demandePrestationRepository = demandePrestationRepository;
        this.etalonnageRepository = etalonnageRepository;
        this.laboEtalonnageRepository = laboEtalonnageRepository;
    }

    public DemandePrestationResponseDTO creer(DemandePrestationRequestDTO dto) {
        Etalonnage etalonnage = etalonnageRepository.findById(dto.etalonnageId())
                .orElseThrow(() -> new RuntimeException("Étalonnage introuvable, id = " + dto.etalonnageId()));

        DemandePrestation demande = new DemandePrestation();
        demande.setEtalonnage(etalonnage);
        demande.setDateDemande(dto.dateDemande());
        demande.setEtatALaLivraison(dto.etatALaLivraison());
        demande.setLieu(dto.lieu());
        demande.setComparaisonInterLabo(dto.comparaisonInterLabo());
        demande.setDeclarationConformite(dto.declarationConformite());
        demande.setRegleDecision(dto.regleDecision());
        demande.setPointsAEtalonner(dto.pointsAEtalonner());
        demande.setNiveauIncertitude(dto.niveauIncertitude());

        if (dto.laboEtalonnageId() != null) {
            LaboEtalonnage labo = laboEtalonnageRepository.findById(dto.laboEtalonnageId())
                    .orElseThrow(() -> new LaboEtalonnageNotFoundException(dto.laboEtalonnageId()));
            demande.setLaboEtalonnage(labo);
        }

        return versDTO(demandePrestationRepository.save(demande));
    }

    public DemandePrestationResponseDTO trouverParEtalonnage(Long etalonnageId) {
        DemandePrestation demande = demandePrestationRepository.findByEtalonnageId(etalonnageId)
                .orElseThrow(() -> new RuntimeException("Aucune demande de prestation pour cet étalonnage, id = " + etalonnageId));
        return versDTO(demande);
    }

    public List<DemandePrestationResponseDTO> lister() {
        return demandePrestationRepository.findAll().stream().map(this::versDTO).toList();
    }

    private DemandePrestationResponseDTO versDTO(DemandePrestation d) {
        return new DemandePrestationResponseDTO(
                d.getId(),
                d.getEtalonnage().getId(),
                d.getEtalonnage().getIntervention().getEquipement().getId(),
                d.getLaboEtalonnage() != null ? d.getLaboEtalonnage().getNom() : null,
                d.getDateDemande(),
                d.getEtatALaLivraison(),
                d.getLieu(),
                d.isComparaisonInterLabo(),
                d.isDeclarationConformite(),
                d.getRegleDecision(),
                d.getPointsAEtalonner(),
                d.getNiveauIncertitude(),
                d.getCreatedAt()
        );
    }
}