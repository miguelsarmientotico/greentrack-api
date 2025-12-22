package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@RequestMapping("/devices")
@Tag(name = "Devices", description = "${api.devices.tag.description:Not Configured}")
public class DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Operation(
        summary = "${api.devices.get-devices.summary:Not Configured}",
        description = "${api.devices.get-devices.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<PagedResponse<DeviceEntity>> getDevices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Page<DeviceEntity> devicesPage = deviceService.getAllDevices(page - 1, limit);
        PagedResponse<DeviceEntity> response = new PagedResponse<>(
            devicesPage.getContent(),
            devicesPage.getNumber() + 1,
            devicesPage.getSize(),
            devicesPage.getTotalElements(),
            devicesPage.getTotalPages(),
            devicesPage.isLast()
        );

        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "${api.devices.filter-devices.summary:Not Configured}",
        description = "${api.devices.filter-devices.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/filter")
    public ResponseEntity<List<DeviceEntity>> filterDevices(
            @RequestParam(required = false) DeviceTypeEnum type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) DeviceStatusEnum status) {
        
        List<DeviceEntity> devices = deviceService.filterDevices(type, brand, status);
        if (devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devices);
    }

    @Operation(
        summary = "${api.devices.create-device.summary:Not Configured}",
        description = "${api.devices.create-device.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.responseCodes.created.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DeviceEntity> createDevice(@Validated(OnCreate.class) @RequestBody DeviceDTO deviceDTO) {
        LOG.info("creando Device");
        DeviceEntity newDevice = deviceService.createDevice(deviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDevice);
    }

    @Operation(
        summary = "${api.devices.get-device-by-id.summary:Not Configured}",
        description = "${api.devices.get-device-by-id.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> getDeviceById(@PathVariable UUID deviceId) {
        return deviceService.getDeviceById(deviceId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado"));
    }

    @Operation(
        summary = "${api.devices.update-device.summary:Not Configured}",
        description = "${api.devices.update-device.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> updateDevice(@PathVariable UUID deviceId, @RequestBody DeviceEntity updates) {
        DeviceEntity updatedDevice = deviceService.updateDevice(deviceId, updates);
        return ResponseEntity.ok(updatedDevice);
    }

    @Operation(
        summary = "${api.devices.delete-device.summary:Not Configured}",
        description = "${api.devices.delete-device.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(
        summary = "${api.devices.bulk-delete.summary:Not Configured}",
        description = "${api.devices.bulk-delete.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteDevicesBulk(@RequestBody Map<String, List<UUID>> payload) {
        List<UUID> ids = payload.get("ids");
        if (ids != null && !ids.isEmpty()) {
            deviceService.deleteDevicesBulk(ids);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
