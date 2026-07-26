package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DocumentResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<DocumentResponseDTO> uploader(
            @RequestParam Long equipementId,
            @RequestParam TypeDocument typeDocument,
            @RequestParam(required = false) Long interventionId,
            @RequestParam(required = false) Long contratMaintenanceId,
            @RequestParam MultipartFile fichier) {

        DocumentResponseDTO reponse = documentService.uploader(
                equipementId, typeDocument, interventionId, contratMaintenanceId, fichier);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping("/equipement/{equipementId}")
    public ResponseEntity<List<DocumentResponseDTO>> listerParEquipement(@PathVariable Long equipementId) {
        return ResponseEntity.ok(documentService.listerParEquipement(equipementId));
    }
}