package com.cert.calibration_hub.equipement;

public class LaboEtalonnageNotFoundException extends RuntimeException {
    public LaboEtalonnageNotFoundException(Long id) {
        super("Laboratoire d'étalonnage introuvable, id = " + id);
    }
}