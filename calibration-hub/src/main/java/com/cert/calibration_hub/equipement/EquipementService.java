package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.EquipementRequestDTO;
import com.cert.calibration_hub.equipement.dto.EquipementResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final PersonnelRepository personnelRepository;
    private final LaboEtalonnageRepository laboEtalonnageRepository;
    private final DocumentRepository documentRepository;
    private final InterventionRepository interventionRepository;
    private final EtalonnageRepository etalonnageRepository;
    private final DemandePrestationRepository demandePrestationRepository;
    private final ContratMaintenanceRepository contratMaintenanceRepository;
    private final AccessoireRepository accessoireRepository;
    private final AlerteResolueRepository alerteResolueRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public EquipementService(EquipementRepository equipementRepository,
                             PersonnelRepository personnelRepository,
                             LaboEtalonnageRepository laboEtalonnageRepository,
                             DocumentRepository documentRepository,
                             InterventionRepository interventionRepository,
                             EtalonnageRepository etalonnageRepository,
                             DemandePrestationRepository demandePrestationRepository,
                             ContratMaintenanceRepository contratMaintenanceRepository,
                             AccessoireRepository accessoireRepository,
                             AlerteResolueRepository alerteResolueRepository) {
        this.equipementRepository = equipementRepository;
        this.personnelRepository = personnelRepository;
        this.laboEtalonnageRepository = laboEtalonnageRepository;
        this.documentRepository = documentRepository;
        this.interventionRepository = interventionRepository;
        this.etalonnageRepository = etalonnageRepository;
        this.demandePrestationRepository = demandePrestationRepository;
        this.contratMaintenanceRepository = contratMaintenanceRepository;
        this.accessoireRepository = accessoireRepository;
        this.alerteResolueRepository = alerteResolueRepository;
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
        return equipementRepository.searchMultiField(motCle)
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

    /**
     * Supprime définitivement l'équipement et TOUT son historique lié
     * (documents, interventions, étalonnages, demandes de prestation,
     * contrats de maintenance, accessoires, alertes résolues), dans le bon
     * ordre pour ne jamais violer une contrainte de clé étrangère.
     */
    @Transactional
    public void supprimerDefinitivement(Long id) {
        Equipement e = getOrThrow(id);

        // 1) Documents rattachés à l'équipement (doivent partir avant les
        //    interventions / contrats qu'ils référencent).
        documentRepository.deleteAll(documentRepository.findByEquipementId(id));

        // 2) Interventions : chacune peut avoir un étalonnage, qui peut
        //    lui-même avoir une demande de prestation.
        List<Intervention> interventions =
                interventionRepository.findByEquipementIdOrderByDateInterventionDesc(id);
        for (Intervention intervention : interventions) {
            etalonnageRepository.findByInterventionId(intervention.getId()).ifPresent(etalonnage -> {
                demandePrestationRepository.findByEtalonnageId(etalonnage.getId())
                        .ifPresent(demandePrestationRepository::delete);
                etalonnageRepository.delete(etalonnage);
            });
        }
        interventionRepository.deleteAll(interventions);

        // 3) Contrats de maintenance
        contratMaintenanceRepository.deleteAll(
                contratMaintenanceRepository.findByEquipementIdOrderByDateFinDesc(id));

        // 4) Accessoires
        accessoireRepository.deleteAll(accessoireRepository.findByEquipementId(id));

        // 5) Alertes déjà résolues concernant cet équipement
        alerteResolueRepository.deleteAll(alerteResolueRepository.findByEquipementId(id));

        // 6) Fichiers physiques uploadés (best effort : ne doit jamais faire
        //    échouer la suppression en base si le disque a un souci).
        supprimerDossierUploads(id);

        // 7) L'équipement lui-même
        equipementRepository.delete(e);
    }

    private void supprimerDossierUploads(Long equipementId) {
        try {
            Path dossier = Paths.get(uploadDir, "equipements", String.valueOf(equipementId));
            if (Files.exists(dossier)) {
                try (var flux = Files.walk(dossier)) {
                    flux.sorted(Comparator.reverseOrder()).forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                            // best effort
                        }
                    });
                }
            }
        } catch (IOException ignored) {
            // best effort : la suppression des fichiers ne bloque jamais la suppression en base.
        }
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


    @Transactional
    public void assignerLaboEtalonnage(Long equipementId, Long laboId) {
        Equipement e = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementNotFoundException(equipementId));
        LaboEtalonnage labo = laboEtalonnageRepository.findById(laboId)
                .orElseThrow(() -> new LaboEtalonnageNotFoundException(laboId));
        e.setLaboEtalonnage(labo);
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
                e.getEtat(), e.getLaboEtalonnage() != null ? e.getLaboEtalonnage().getNom() : null,
                e.getObservations(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }


}