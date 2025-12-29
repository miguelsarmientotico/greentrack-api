package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.dashboard.DashboardResponseDTO;
import com.greentrack.greentrack_api.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "${api.dashboard.tag.description:Not Configured}")
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
        summary = "${api.dashboard.get-summary.summary:Not Configured}",
        description = "${api.dashboard.get-summary.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "403", description = "${api.responseCodes.forbidden.description:Not Configured}")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(Authentication authentication) {
        String currentUsername = authentication.getName();
        LOG.info("Solicitud de Dashboard iniciada por el usuario: {}", currentUsername);
        DashboardResponseDTO dashboards = dashboardService.getDashboard();
        LOG.info("Dashboard generado exitosamente para '{}'.", currentUsername);
        return ResponseEntity.ok(dashboards);
    }
}
