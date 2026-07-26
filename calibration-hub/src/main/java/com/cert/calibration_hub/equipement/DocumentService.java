package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DocumentResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EquipementRepository equipementRepository;
    private final InterventionRepository interventionRepository;
    private final ContratMaintenanceRepository contratMaintenanceRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository,
                           EquipementRepository equipementRepository,
                           InterventionRepository interventionRepository,
                           ContratMaintenanceRepository contratMaintenanceRepository) {
        this.documentRepository = documentRepository;
        this.equipementRepository = equipementRepository;
        this.interventionRepository = interventionRepository;
        this.contratMaintenanceRepository = contratMaintenanceRepository;
    }

    public DocumentResponseDTO uploader(Long equipementId, TypeDocument type,
                                        Long interventionId, Long contratMaintenanceId,
                                        MultipartFile fichier) {

        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementNotFoundException(equipementId));

        String nomOriginal = fichier.getOriginalFilename();
        String nomStocke = UUID.randomUUID() + "_" + nomOriginal;

        try {
            Path dossierEquipement = Paths.get(uploadDir, "equipements", String.valueOf(equipementId));
            Files.createDirectories(dossierEquipement);

            Path cheminComplet = dossierEquipement.resolve(nomStocke);
            fichier.transferTo(cheminComplet);

            Document document = new Document();
            document.setEquipement(equipement);
            document.setTypeDocument(type);
            document.setNomFichier(nomOriginal);
            document.setCheminStockage(cheminComplet.toString());

            if (interventionId != null) {
                document.setIntervention(interventionRepository.findById(interventionId).orElse(null));
            }
            if (contratMaintenanceId != null) {
                document.setContratMaintenance(contratMaintenanceRepository.findById(contratMaintenanceId).orElse(null));
            }

            Document sauvegarde = documentRepository.save(document);
            return versDTO(sauvegarde);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier : " + e.getMessage(), e);
        }
    }

    public List<DocumentResponseDTO> listerParEquipement(Long equipementId) {
        return documentRepository.findByEquipementId(equipementId)
                .stream()
                .map(this::versDTO)
                .toList();
    }


    // DocumentService.java
    public void delete(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Document introuvable, id = " + id);
        }
        documentRepository.deleteById(id);
    }

    private DocumentResponseDTO versDTO(Document d) {
        return new DocumentResponseDTO(
                d.getId(),
                d.getEquipement().getId(),
                d.getTypeDocument(),
                d.getNomFichier(),
                d.getIntervention() != null ? d.getIntervention().getId() : null,
                d.getContratMaintenance() != null ? d.getContratMaintenance().getId() : null,
                d.getUploadedAt()
        );
    }
}