package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.DashboardResponseDTO;
import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.service.DashboardService;
import com.greentrack.greentrack_api.service.DeviceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        DashboardResponseDTO dashboards = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboards);
    }
}
