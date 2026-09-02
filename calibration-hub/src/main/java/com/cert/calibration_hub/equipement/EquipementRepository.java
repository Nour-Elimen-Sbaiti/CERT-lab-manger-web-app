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

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Equipement e WHERE " +
            "LOWER(e.designation) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(e.marque) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(e.modele) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(e.codeIdentifiant) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(e.numeroSerie) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
            "LOWER(e.codeEmplacement) LIKE LOWER(CONCAT('%', :motCle, '%'))")
    List<Equipement> searchMultiField(String motCle);

    List<Equipement> findByGarantieJusquAuLessThanEqual(LocalDate date);
}