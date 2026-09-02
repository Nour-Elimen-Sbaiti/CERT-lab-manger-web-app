package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.ContratMaintenanceRequestDTO;
import com.cert.calibration_hub.equipement.dto.ContratMaintenanceResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratMaintenanceService {

    private final ContratMaintenanceRepository contratMaintenanceRepository;
    private final EquipementRepository equipementRepository;
    private final LaboEtalonnageRepository laboEtalonnageRepository;

    public ContratMaintenanceService(ContratMaintenanceRepository contratMaintenanceRepository,
                                     EquipementRepository equipementRepository,
                                     LaboEtalonnageRepository laboEtalonnageRepository) {
        this.contratMaintenanceRepository = contratMaintenanceRepository;
        this.equipementRepository = equipementRepository;
        this.laboEtalonnageRepository = laboEtalonnageRepository;
    }

    public ContratMaintenanceResponseDTO creer(ContratMaintenanceRequestDTO dto) {
        Equipement equipement = equipementRepository.findById(dto.equipementId())
                .orElseThrow(() -> new EquipementNotFoundException(dto.equipementId()));

        ContratMaintenance contrat = new ContratMaintenance();
        contrat.setEquipement(equipement);
        contrat.setPrestataire(dto.prestataire());
        if (dto.laboEtalonnageId() != null) {
            LaboEtalonnage labo = laboEtalonnageRepository.findById(dto.laboEtalonnageId())
                    .orElseThrow(() -> new LaboEtalonnageNotFoundException(dto.laboEtalonnageId()));
            contrat.setLaboEtalonnage(labo);
        }
        contrat.setDateDebut(dto.dateDebut());
        contrat.setDateFin(dto.dateFin());
        contrat.setPeriodeValeur(dto.periodeValeur());
        contrat.setPeriodeUnite(dto.periodeUnite());

        ContratMaintenance sauvegarde = contratMaintenanceRepository.save(contrat);
        return versDTO(sauvegarde);
    }

    public List<ContratMaintenanceResponseDTO> listerParEquipement(Long equipementId) {
        return contratMaintenanceRepository.findByEquipementIdOrderByDateFinDesc(equipementId)
                .stream()
                .map(this::versDTO)
                .toList();
    }

    private ContratMaintenanceResponseDTO versDTO(ContratMaintenance c) {
        return new ContratMaintenanceResponseDTO(
                c.getId(),
                c.getEquipement().getId(),
                c.getPrestataire(),
                c.getLaboEtalonnage() != null ? c.getLaboEtalonnage().getNom() : null,
                c.getDateDebut(),
                c.getDateFin(),
                c.getPeriodeValeur(),
                c.getPeriodeUnite(),
                c.estValide()
        );
    }


}