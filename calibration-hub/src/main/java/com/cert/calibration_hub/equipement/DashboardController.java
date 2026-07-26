package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.DashboardDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard")
    public DashboardDTO obtenirDashboard() {
        return dashboardService.obtenirDashboard();
    }
}