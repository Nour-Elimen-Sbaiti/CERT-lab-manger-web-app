package com.cert.calibration_hub.common;

import com.cert.calibration_hub.equipement.EquipementNotFoundException;
import com.cert.calibration_hub.equipement.InterventionInvalideException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.cert.calibration_hub.equipement.LaboEtalonnageNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EquipementNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> erreurs.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> corps = body("Données invalides.", HttpStatus.BAD_REQUEST);
        corps.put("erreurs", erreurs);
        return ResponseEntity.badRequest().body(corps);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("Erreur interne : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String messageComplet = ex.getMostSpecificCause().getMessage();
        String message;

        if (messageComplet != null && messageComplet.contains("duplicate key")) {
            message = "Cette valeur existe déjà (code identifiant, numéro de série, ou autre champ unique).";
        } else if (messageComplet != null && messageComplet.contains("violates foreign key constraint")) {
            message = "Impossible de supprimer : cette donnée est encore référencée ailleurs (ex: historique, interventions liées).";
        } else {
            message = "Cette opération viole une contrainte d'intégrité de la base de données.";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(InterventionInvalideException.class)
    public ResponseEntity<Map<String, Object>> handleInterventionInvalide(InterventionInvalideException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(LaboEtalonnageNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLaboNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    private Map<String, Object> body(String message, HttpStatus status) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("timestamp", LocalDateTime.now());
        corps.put("status", status.value());
        corps.put("message", message);
        return corps;
    }
}