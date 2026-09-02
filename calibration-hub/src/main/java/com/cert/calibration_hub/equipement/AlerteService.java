package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.AlerteDTO;
import com.cert.calibration_hub.equipement.dto.AlerteResolutionRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlerteService {

    private static final String TYPE_ETALONNAGE = "ETALONNAGE";
    private static final String TYPE_CONTRAT_MAINTENANCE = "CONTRAT_MAINTENANCE";
    private static final String TYPE_GARANTIE = "GARANTIE";

    private final EtalonnageRepository etalonnageRepository;
    private final ContratMaintenanceRepository contratMaintenanceRepository;
    private final EquipementRepository equipementRepository;
    private final AlerteResolueRepository alerteResolueRepository;

    public AlerteService(EtalonnageRepository etalonnageRepository,
                         ContratMaintenanceRepository contratMaintenanceRepository,
                         EquipementRepository equipementRepository,
                         AlerteResolueRepository alerteResolueRepository) {
        this.etalonnageRepository = etalonnageRepository;
        this.contratMaintenanceRepository = contratMaintenanceRepository;
        this.equipementRepository = equipementRepository;
        this.alerteResolueRepository = alerteResolueRepository;
    }

    @Transactional(readOnly = true)
    public List<AlerteDTO> obtenirAlertes(int joursSeuil, boolean inclureResolues) {
        LocalDate seuil = LocalDate.now().plusDays(joursSeuil);
        List<AlerteDTO> alertes = new ArrayList<>();

        // ---- Étalonnages qui expirent ou ont expiré ----
        for (Etalonnage etalonnage : etalonnageRepository.findByDateValiditeLessThanEqual(seuil)) {
            if (etalonnage.getDateValidite() == null) continue;
            Equipement e = etalonnage.getIntervention().getEquipement();
            String key = construireCle(TYPE_ETALONNAGE, etalonnage.getId(), etalonnage.getDateValidite());
            AlerteResolue resolution = alerteResolueRepository.findByAlerteKey(key).orElse(null);
            if (resolution != null && !inclureResolues) continue;

            long jours = ChronoUnit.DAYS.between(LocalDate.now(), etalonnage.getDateValidite());
            alertes.add(new AlerteDTO(
                    key,
                    TYPE_ETALONNAGE,
                    etalonnage.getId(),
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    etalonnage.getDateValidite(),
                    jours,
                    "Étalonnage réalisé le " + formater(etalonnage.getIntervention().getDateIntervention()),
                    resolution != null,
                    resolution != null ? resolution.getObservation() : null,
                    resolution != null ? resolution.getResolvedBy() : null,
                    resolution != null ? resolution.getResolvedAt() : null
            ));
        }

        // ---- Contrats de maintenance qui expirent ou ont expiré ----
        for (ContratMaintenance contrat : contratMaintenanceRepository.findByDateFinLessThanEqual(seuil)) {
            Equipement e = contrat.getEquipement();
            String key = construireCle(TYPE_CONTRAT_MAINTENANCE, contrat.getId(), contrat.getDateFin());
            AlerteResolue resolution = alerteResolueRepository.findByAlerteKey(key).orElse(null);
            if (resolution != null && !inclureResolues) continue;

            long jours = ChronoUnit.DAYS.between(LocalDate.now(), contrat.getDateFin());
            alertes.add(new AlerteDTO(
                    key,
                    TYPE_CONTRAT_MAINTENANCE,
                    contrat.getId(),
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    contrat.getDateFin(),
                    jours,
                    "Contrat avec " + contrat.getPrestataire(),
                    resolution != null,
                    resolution != null ? resolution.getObservation() : null,
                    resolution != null ? resolution.getResolvedBy() : null,
                    resolution != null ? resolution.getResolvedAt() : null
            ));
        }

        // ---- Garanties qui expirent ou ont expiré ----
        for (Equipement e : equipementRepository.findByGarantieJusquAuLessThanEqual(seuil)) {
            if (e.getGarantieJusquAu() == null) continue;
            String key = construireCle(TYPE_GARANTIE, e.getId(), e.getGarantieJusquAu());
            AlerteResolue resolution = alerteResolueRepository.findByAlerteKey(key).orElse(null);
            if (resolution != null && !inclureResolues) continue;

            long jours = ChronoUnit.DAYS.between(LocalDate.now(), e.getGarantieJusquAu());
            alertes.add(new AlerteDTO(
                    key,
                    TYPE_GARANTIE,
                    e.getId(),
                    e.getId(),
                    e.getDesignation(),
                    e.getCodeIdentifiant(),
                    e.getGarantieJusquAu(),
                    jours,
                    "Garantie fournisseur",
                    resolution != null,
                    resolution != null ? resolution.getObservation() : null,
                    resolution != null ? resolution.getResolvedBy() : null,
                    resolution != null ? resolution.getResolvedAt() : null
            ));
        }

        alertes.sort(Comparator.comparing(AlerteDTO::resolue).thenComparingLong(AlerteDTO::joursRestants));
        return alertes;
    }

    public List<AlerteDTO> obtenirAlertes(int joursSeuil) {
        return obtenirAlertes(joursSeuil, false);
    }

    @Transactional
    public AlerteDTO resoudreAlerte(String alerteId, AlerteResolutionRequestDTO request, String resolvedBy) {
        AlerteDTO alerte = trouverAlerteParId(alerteId);
        String observation = request != null ? request.observation() : null;

        AlerteResolue resolution = alerteResolueRepository.findByAlerteKey(alerteId)
                .orElseGet(() -> new AlerteResolue(
                        alerte.id(),
                        alerte.type(),
                        alerte.sourceId(),
                        alerte.equipementId(),
                        alerte.dateEcheance(),
                        observation,
                        resolvedBy
                ));

        resolution.setObservation(observation);
        resolution.setResolvedBy(resolvedBy);
        AlerteResolue sauvegardee = alerteResolueRepository.save(resolution);

        return new AlerteDTO(
                alerte.id(),
                alerte.type(),
                alerte.sourceId(),
                alerte.equipementId(),
                alerte.equipementDesignation(),
                alerte.equipementCodeIdentifiant(),
                alerte.dateEcheance(),
                alerte.joursRestants(),
                alerte.details(),
                true,
                sauvegardee.getObservation(),
                sauvegardee.getResolvedBy(),
                sauvegardee.getResolvedAt()
        );
    }

    @Transactional
    public void rouvrirAlerte(String alerteId) {
        if (!alerteResolueRepository.existsByAlerteKey(alerteId)) {
            throw new IllegalArgumentException("Alerte résolue introuvable : " + alerteId);
        }
        alerteResolueRepository.deleteByAlerteKey(alerteId);
    }

    @Transactional(readOnly = true)
    public AlerteDTO trouverAlerteParId(String alerteId) {
        return obtenirAlertes(3650, true).stream()
                .filter(a -> a.id().equals(alerteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Alerte introuvable : " + alerteId));
    }

    private String construireCle(String type, Long sourceId, LocalDate dateEcheance) {
        return type + "-" + sourceId + "-" + dateEcheance;
    }

    private String formater(LocalDate date) {
        return date != null ? date.toString() : "date inconnue";
    }
}
