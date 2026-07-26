package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DashboardDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EquipementRepository equipementRepository;
    private final PersonnelRepository personnelRepository;
    private final AccessoireRepository accessoireRepository;
    private final AlerteService alerteService;

    public DashboardService(EquipementRepository equipementRepository,
                            PersonnelRepository personnelRepository,
                            AccessoireRepository accessoireRepository,
                            AlerteService alerteService) {
        this.equipementRepository = equipementRepository;
        this.personnelRepository = personnelRepository;
        this.accessoireRepository = accessoireRepository;
        this.alerteService = alerteService;
    }

    public DashboardDTO obtenirDashboard() {
        var equipements = equipementRepository.findAll();

        Map<String, Long> repartition = equipements.stream()
                .collect(Collectors.groupingBy(e -> e.getEtat().name(), Collectors.counting()));

        long alertesActives = alerteService.obtenirAlertes(30).size();

        return new DashboardDTO(
                equipements.size(),
                repartition,
                personnelRepository.count(),
                accessoireRepository.count(),
                alertesActives
        );
    }
}