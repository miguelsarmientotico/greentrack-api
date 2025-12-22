package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.DeviceMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final DeviceRepository repository;
    private final DeviceMapper mapper;

    public DeviceService(
        DeviceRepository repository,
        DeviceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<DeviceEntity> getAllDevices(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<DeviceEntity> filterDevices(DeviceTypeEnum type, String brand, DeviceStatusEnum status) {
        
        DeviceEntity probe = new DeviceEntity();
        if (type != null) probe.setDeviceType(type);
        if (brand != null) probe.setBrand(brand);
        if (status != null) probe.setDeviceStatus(status);
        
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return repository.findAll(Example.of(probe, matcher));
    }

    @Transactional
    public DeviceEntity createDevice(DeviceDTO device) {
        if (repository.existsByName(device.getName())) {
            throw new InvalidInputException("El nombre de usuario ya existe.");
        }
        DeviceEntity deviceEntity = mapper.apiToEntity(device);
    
        try {
            return repository.save(deviceEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidInputException(
                "Could not create user."
            );
        }
    }

    public Optional<DeviceEntity> getDeviceById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public DeviceEntity updateDevice(UUID id, DeviceEntity updates) {
        return repository.findById(id).map(device -> {
            if (updates.getName() != null) device.setName(updates.getName());
            if (updates.getBrand() != null) device.setBrand(updates.getBrand());
            if (updates.getDeviceStatus() != null) device.setDeviceStatus(updates.getDeviceStatus());
            if (updates.getDeviceType() != null) device.setDeviceType(updates.getDeviceType());
            return repository.save(device);
        }).orElseThrow(() -> new NotFoundException("Dispositivo no encontrado"));
    }

    @Transactional
    public void deleteDevice(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    @Transactional
    public void deleteDevicesBulk(List<UUID> ids) {
        repository.deleteAllById(ids);
    }
}
