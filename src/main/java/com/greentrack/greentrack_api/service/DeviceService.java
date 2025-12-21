package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.repository.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Page<DeviceEntity> getAllDevices(int page, int size) {
        return deviceRepository.findAll(PageRequest.of(page, size));
    }

    // Lógica para el endpoint /filter
    public List<DeviceEntity> filterDevices(DeviceTypeEnum type, String brand, DeviceStatusEnum status) {
        // Creamos un objeto "probe" (sonda/molde) con los datos que tenemos
        DeviceEntity probe = new DeviceEntity();
        if (type != null) probe.setDeviceType(type);
        if (brand != null) probe.setBrand(brand);
        if (status != null) probe.setDeviceStatus(status);

        // Configuramos cómo comparar (ignorando nulos y mayúsculas para strings)
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return deviceRepository.findAll(Example.of(probe, matcher));
    }

    @Transactional
    public DeviceEntity createDevice(DeviceEntity device) {
        return deviceRepository.save(device);
    }

    public Optional<DeviceEntity> getDeviceById(UUID id) {
        return deviceRepository.findById(id);
    }

    @Transactional
    public DeviceEntity updateDevice(UUID id, DeviceEntity updates) {
        return deviceRepository.findById(id).map(device -> {
            if (updates.getName() != null) device.setName(updates.getName());
            if (updates.getBrand() != null) device.setBrand(updates.getBrand());
            if (updates.getDeviceStatus() != null) device.setDeviceStatus(updates.getDeviceStatus());
            if (updates.getDeviceType() != null) device.setDeviceType(updates.getDeviceType());
            return deviceRepository.save(device);
        }).orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));
    }

    @Transactional
    public void deleteDevice(UUID id) {
        deviceRepository.deleteById(id);
    }

    @Transactional
    public void deleteDevicesBulk(List<UUID> ids) {
        deviceRepository.deleteAllById(ids);
    }
}
