package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessoireRepository extends JpaRepository<Accessoire, Long> {

    List<Accessoire> findByEquipementId(Long equipementId);
}