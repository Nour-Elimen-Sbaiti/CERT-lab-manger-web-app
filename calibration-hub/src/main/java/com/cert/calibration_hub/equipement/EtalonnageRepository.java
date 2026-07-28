package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EtalonnageRepository extends JpaRepository<Etalonnage, Long> {

    Optional<Etalonnage> findByInterventionId(Long interventionId);

    // pour les alertes : tous les étalonnages qui expirent avant une date donnée
    List<Etalonnage> findByDateValiditeLessThanEqual(LocalDate date);
}