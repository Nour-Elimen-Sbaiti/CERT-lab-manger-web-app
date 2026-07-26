package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratMaintenanceRepository extends JpaRepository<ContratMaintenance, Long> {

    // le plus récent d'abord = le contrat "actuel" en tête de liste
    List<ContratMaintenance> findByEquipementIdOrderByDateFinDesc(Long equipementId);
}