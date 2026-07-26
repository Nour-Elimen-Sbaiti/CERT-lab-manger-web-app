package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AlerteDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlerteService {

    private final EtalonnageRepository etalonnageRepository;
    private final ContratMaintenanceRepository contratMaintenanceRepository;
    private final EquipementRepository equipementRepository;

    public AlerteService(EtalonnageRepository etalonnageRepository,
                         ContratMaintenanceRepository contratMaintenanceRepository,
                         EquipementRepository equipementRepository) {
        this.etalonnageRepository = etalonnageRepository;
        this.contratMaintenanceRepository = contratMaintenanceRepository;
        this.equipementRepository = equipementRepository;
    }

    public List<AlerteDTO> obtenirAlertes(int joursSeuil) {
        LocalDate seuil = LocalDate.now().plusDays(joursSeuil);
        List<AlerteDTO> alertes = new ArrayList<>();

        // ---- Étalonnages qui expirent ou ont expiré ----
        for (Etalonnage etalonnage : etalonnageRepository.findByDateValiditeLessThanEqual(seuil)) {
            if (etalonnage.getDateValidite() == null) continue;
            Equipement e = etalonnage.getIntervention().getEquipement();
            long jours = ChronoUnit.DAYS.between(LocalDate.now(), etalonnage.getDateValidite());
            alertes.add(new AlerteDTO(
                    "ETALONNAGE",
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    etalonnage.getDateValidite(),
                    jours,
                    "Étalonnage réalisé le " + formater(etalonnage.getIntervention().getDateIntervention())
            ));
        }

        // ---- Contrats de maintenance qui expirent ou ont expiré ----
        for (ContratMaintenance contrat : contratMaintenanceRepository.findByDateFinLessThanEqual(seuil)) {
            Equipement e = contrat.getEquipement();
            long jours = ChronoUnit.DAYS.between(LocalDate.now(), contrat.getDateFin());
            alertes.add(new AlerteDTO(
                    "CONTRAT_MAINTENANCE",
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    contrat.getDateFin(),
                    jours,
                    "Contrat avec " + contrat.getPrestataire()
            ));
        }

        // ---- Garanties qui expirent ou ont expiré ----
        for (Equipement e : equipementRepository.findByGarantieJusquAuLessThanEqual(seuil)) {
            if (e.getGarantieJusquAu() == null) continue;
            long jours = ChronoUnit.DAYS.between(LocalDate.now(), e.getGarantieJusquAu());
            alertes.add(new AlerteDTO(
                    "GARANTIE",
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    e.getGarantieJusquAu(),
                    jours,
                    "Garantie fournisseur"
            ));
        }

        alertes.sort(Comparator.comparingLong(AlerteDTO::joursRestants));
        return alertes;
    }

    private String formater(LocalDate date) {
        return date != null ? date.toString() : "date inconnue";
    }
}