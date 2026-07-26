package com.cert.calibration_hub.equipement;

public class EquipementNotFoundException extends RuntimeException {
    public EquipementNotFoundException(Long id) {
        super("Équipement introuvable, id = " + id);
    }
}