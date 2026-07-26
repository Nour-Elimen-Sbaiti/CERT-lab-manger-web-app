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
    public ResponseEntity<Map<String, Object>> handleDuplicate(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body("Un équipement avec ce code identifiant ou ce numéro de série existe déjà.", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(InterventionInvalideException.class)
    public ResponseEntity<Map<String, Object>> handleInterventionInvalide(InterventionInvalideException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    private Map<String, Object> body(String message, HttpStatus status) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("timestamp", LocalDateTime.now());
        corps.put("status", status.value());
        corps.put("message", message);
        return corps;
    }
}