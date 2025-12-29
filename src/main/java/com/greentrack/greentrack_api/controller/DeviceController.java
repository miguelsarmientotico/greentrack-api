package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.dto.device.DeviceFilterDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<PagedResponse<DeviceEntity>> getAllDevices(
        @ModelAttribute DeviceFilterDTO filter,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        Page<DeviceEntity> devicesPage = deviceService.searchDevicesAdvanced(filter, pageable);
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
        return ResponseEntity.ok(deviceService.getDeviceById(deviceId));
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
    public ResponseEntity<DeviceEntity> createDevice(
        @Validated(OnCreate.class) @RequestBody DeviceDTO deviceDTO,
        Authentication authentication
    ) {
        LOG.info("Usuario '{}' registrando nuevo dispositivo: {}", authentication.getName(), deviceDTO.getName());
        DeviceEntity newDevice = deviceService.createDevice(deviceDTO);
        LOG.debug("Dispositivo creado con ID interno: {}", newDevice.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newDevice);
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
    public ResponseEntity<DeviceEntity> updateDevice(
        @PathVariable UUID deviceId,
        @RequestBody DeviceEntity updates,
        Authentication authentication
    ) {
        LOG.info("Usuario '{}' modificando dispositivo ID: {}", authentication.getName(), deviceId);
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
    public ResponseEntity<Void> deleteDevice(
        @PathVariable UUID deviceId,
        Authentication authentication
    ) {
        LOG.warn("⚠️ Usuario '{}' ELIMINANDO Device ID: {}", authentication.getName(), deviceId);
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
    public ResponseEntity<Void> deleteDevicesBulk(
        @RequestBody Map<String, List<UUID>> payload,
        Authentication authentication
    ) {
        List<UUID> ids = payload.get("ids");
        int count = (ids != null) ? ids.size() : 0;
        LOG.warn("⚠️ Usuario '{}' solicitó ELIMINACIÓN MASIVA de {} devices.", authentication.getName(), count);
        deviceService.deleteDevicesBulk(ids);
        return ResponseEntity.ok().build();
    }
}
