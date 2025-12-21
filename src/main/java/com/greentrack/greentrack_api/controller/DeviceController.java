package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.service.DeviceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceEntity>> getDevices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Page<DeviceEntity> devices = deviceService.getAllDevices(page - 1, limit);
        if (devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devices.getContent());
    }
    
    // Endpoint específico para filtros múltiples
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

    @PostMapping
    public ResponseEntity<DeviceEntity> createDevice(@RequestBody DeviceEntity device) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.createDevice(device));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> getDeviceById(@PathVariable UUID deviceId) {
        return deviceService.getDeviceById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> updateDevice(@PathVariable UUID deviceId, @RequestBody DeviceEntity updates) {
         try {
            return ResponseEntity.ok(deviceService.updateDevice(deviceId, updates));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.noContent().build();
    }
    
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
