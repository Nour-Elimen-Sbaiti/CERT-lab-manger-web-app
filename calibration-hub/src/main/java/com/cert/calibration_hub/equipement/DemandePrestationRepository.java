package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandePrestationRepository extends JpaRepository<DemandePrestation, Long> {

    Optional<DemandePrestation> findByEtalonnageId(Long etalonnageId);
}