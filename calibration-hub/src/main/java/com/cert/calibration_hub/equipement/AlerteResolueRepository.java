package com.cert.calibration_hub.equipement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlerteResolueRepository extends JpaRepository<AlerteResolue, Long> {

    Optional<AlerteResolue> findByAlerteKey(String alerteKey);

    boolean existsByAlerteKey(String alerteKey);

    void deleteByAlerteKey(String alerteKey);

    List<AlerteResolue> findByEquipementId(Long equipementId);
}
