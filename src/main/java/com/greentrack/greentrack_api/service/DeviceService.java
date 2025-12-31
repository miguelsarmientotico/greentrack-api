package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.dto.device.DeviceFilterDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.exception.BadRequestException;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.DeviceMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.specifications.DeviceSpecifications;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {

  private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

  private final DeviceRepository repository;
  private final DeviceMapper mapper;

  public DeviceService(DeviceRepository repository, DeviceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  private static final List<String> CAMPOS_VALIDOS =
      List.of("id", "name", "type", "brand", "status");

  @Transactional(readOnly = true)
  public Page<DeviceEntity> searchDevicesAdvanced(DeviceFilterDTO filter, Pageable pageable) {
    if (pageable.getSort().isSorted()) {
      for (Sort.Order order : pageable.getSort()) {
        if (!CAMPOS_VALIDOS.contains(order.getProperty())) {
          throw new InvalidInputException(
              "Campo de ordenamiento no válido: " + order.getProperty());
        }
      }
    }
    Specification<DeviceEntity> spec = DeviceSpecifications.getDevices(filter);
    return repository.findAll(spec, pageable);
  }

  @Transactional(readOnly = true)
  public DeviceEntity getDeviceById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> {
              LOG.warn("Dispositivo no encontrado con ID: {}", id);
              return new NotFoundException("Equipo no encontrado con ID: " + id);
            });
  }

  @Transactional
  public DeviceEntity createDevice(DeviceDTO device) {
    LOG.info("Intentando crear dispositivo: {}", device.getName());
    if (repository.existsByName(device.getName())) {
      LOG.warn("Intento de crear dispositivo duplicado: {}", device.getName());
      throw new InvalidInputException("El nombre del dispositivo ya existe.");
    }
    DeviceEntity deviceEntity = mapper.apiToEntity(device);

    try {
      DeviceEntity savedDevice = repository.save(deviceEntity);
      LOG.info("Dispositivo creado exitosamente con ID: {}", savedDevice.getId());
      return savedDevice;
    } catch (DataIntegrityViolationException ex) {
      LOG.error("Error de integridad al guardar dispositivo: {}", ex.getMessage());
      throw new InvalidInputException("Could not create device. Data integrity violation.");
    }
  }

  @Transactional
  public DeviceEntity updateDevice(UUID id, DeviceEntity updates) {
    LOG.info("Actualizando dispositivo ID: {}", id);
    DeviceEntity device = getDeviceById(id);

    if (updates.getName() != null) device.setName(updates.getName());
    if (updates.getBrand() != null) device.setBrand(updates.getBrand());
    if (updates.getStatus() != null) device.setStatus(updates.getStatus());
    if (updates.getType() != null) device.setType(updates.getType());

    return repository.save(device);
  }

  @Transactional
  public void deleteDevice(UUID id) {
    LOG.warn("Solicitud de eliminación para dispositivo ID: {}", id);
    if (!repository.existsById(id)) {
      LOG.error("Intento de eliminar dispositivo inexistente ID: {}", id);
      throw new NotFoundException("No se puede eliminar. Dispositivo no encontrado.");
    }
    repository.deleteById(id);
    LOG.info("Dispositivo eliminado correctamente ID: {}", id);
  }

  @Transactional
  public void deleteDevicesBulk(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      throw new BadRequestException("La lista de IDs es requerida para eliminación masiva.");
    }
    LOG.info("Eliminando lote de {} dispositivos.", ids.size());
    repository.deleteAllById(ids);
  }
}
