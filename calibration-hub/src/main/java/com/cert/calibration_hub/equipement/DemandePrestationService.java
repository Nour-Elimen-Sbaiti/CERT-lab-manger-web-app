package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DemandePrestationRequestDTO;
import com.cert.calibration_hub.equipement.dto.DemandePrestationResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public DemandePrestationResponseDTO creer(DemandePrestationRequestDTO dto) {
        DemandePrestation demande = new DemandePrestation();
        appliquerDto(demande, dto);
        return versDTO(demandePrestationRepository.save(demande));
    }

    @Transactional(readOnly = true)
    public DemandePrestationResponseDTO trouverParId(Long id) {
        return versDTO(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public DemandePrestationResponseDTO trouverParEtalonnage(Long etalonnageId) {
        DemandePrestation demande = demandePrestationRepository.findByEtalonnageId(etalonnageId)
                .orElseThrow(() -> new RuntimeException("Aucune demande de prestation pour cet étalonnage, id = " + etalonnageId));
        return versDTO(demande);
    }

    @Transactional(readOnly = true)
    public List<DemandePrestationResponseDTO> lister() {
        return demandePrestationRepository.findAll().stream().map(this::versDTO).toList();
    }

    @Transactional
    public DemandePrestationResponseDTO modifier(Long id, DemandePrestationRequestDTO dto) {
        DemandePrestation demande = getOrThrow(id);
        appliquerDto(demande, dto);
        return versDTO(demandePrestationRepository.save(demande));
    }

    @Transactional
    public void supprimer(Long id) {
        DemandePrestation demande = getOrThrow(id);
        demandePrestationRepository.delete(demande);
    }

    private void appliquerDto(DemandePrestation demande, DemandePrestationRequestDTO dto) {
        Etalonnage etalonnage = etalonnageRepository.findById(dto.etalonnageId())
                .orElseThrow(() -> new RuntimeException("Étalonnage introuvable, id = " + dto.etalonnageId()));

        demandePrestationRepository.findByEtalonnageId(dto.etalonnageId())
                .filter(existante -> demande.getId() == null || !existante.getId().equals(demande.getId()))
                .ifPresent(existante -> {
                    throw new IllegalArgumentException("Une demande de prestation existe déjà pour cet étalonnage.");
                });

        demande.setEtalonnage(etalonnage);
        if (dto.dateDemande() != null || demande.getId() == null) {
            demande.setDateDemande(dto.dateDemande());
        }
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
        } else {
            demande.setLaboEtalonnage(null);
        }
    }

    private DemandePrestation getOrThrow(Long id) {
        return demandePrestationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de prestation introuvable, id = " + id));
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