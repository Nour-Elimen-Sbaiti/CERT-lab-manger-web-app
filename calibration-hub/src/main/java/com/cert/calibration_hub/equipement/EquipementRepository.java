package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    Optional<Equipement> findByCodeIdentifiant(String codeIdentifiant);

    Optional<Equipement> findByNumeroSerie(String numeroSerie);

    List<Equipement> findByEtat(EtatEquipement etat);

    List<Equipement> findByDesignationContainingIgnoreCase(String motCle);
    List<Equipement> findByGarantieJusquAuLessThanEqual(LocalDate date);
}