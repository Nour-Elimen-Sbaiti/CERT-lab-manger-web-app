package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findByEquipementIdOrderByDateInterventionDesc(Long equipementId);

    List<Intervention> findByType(TypeIntervention type);

    List<Intervention> findByStatut(StatutIntervention statut);
}