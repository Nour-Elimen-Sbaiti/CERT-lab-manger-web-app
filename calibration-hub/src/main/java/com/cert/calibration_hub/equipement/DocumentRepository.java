package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByEquipementId(Long equipementId);

    List<Document> findByInterventionId(Long interventionId);

    List<Document> findByContratMaintenanceId(Long contratMaintenanceId);
}