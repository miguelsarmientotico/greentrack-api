package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.loan.LoanDTO;
import com.greentrack.greentrack_api.dto.loan.LoanFilterDTO;
import com.greentrack.greentrack_api.entity.*;
import com.greentrack.greentrack_api.exception.BadRequestException;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.LoanMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.LoanRepository;
import com.greentrack.greentrack_api.repository.UserRepository;
import com.greentrack.greentrack_api.repository.specifications.LoanSpecifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loanRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final LoanMapper mapper;

    public LoanService(
        LoanRepository loanRepository,
        DeviceRepository deviceRepository,
        UserRepository userRepository,
        LoanMapper mapper
    ) {
        this.loanRepository = loanRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    private static final List<String> CAMPOS_VALIDOS = List.of("id", "employeeFullName", "deviceName");

    @Transactional(readOnly = true)
    public Page<LoanEntity> searchLoansAdvanced(LoanFilterDTO filter, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                if (!CAMPOS_VALIDOS.contains(order.getProperty())) {
                    throw new InvalidInputException("Campo de ordenamiento no válido: " + order.getProperty());
                }
            }
        }
        Specification<LoanEntity> spec = LoanSpecifications.getLoans(filter);
        return loanRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public LoanEntity getLoanById(UUID id) {
        return loanRepository.findById(id)
        .orElseThrow(() -> {
            LOG.warn("Consulta fallida: Préstamo no encontrado ID: {}", id);
            return new NotFoundException("Préstamo no encontrado con ID: " + id);
        });
    }

    @Transactional
    public LoanEntity createLoan(LoanDTO loanDto) {
        LOG.info("Iniciando solicitud de préstamo. User: {}, Device: {}", loanDto.getEmployeeId(), loanDto.getDeviceId());
        DeviceEntity device = deviceRepository.findById(loanDto.getDeviceId())
        .orElseThrow(() -> {
            LOG.warn("Error al crear préstamo: Dispositivo {} no existe", loanDto.getDeviceId());
            return new NotFoundException("Dispositivo no encontrado");
        });
        if (device.getStatus() != DeviceStatusEnum.DISPONIBLE) {
            LOG.warn("Préstamo denegado. El dispositivo {} está en estado {}", device.getName(), device.getStatus());
            throw new InvalidInputException("El dispositivo no está disponible. Estado actual: " + device.getStatus());
        }
        UserEntity employee = userRepository.findById(loanDto.getEmployeeId())
        .orElseThrow(() -> {
            LOG.warn("Error al crear préstamo: Empleado {} no existe", loanDto.getEmployeeId());
            return new NotFoundException("Empleado no encontrado");
        });
        LoanEntity loanEntity = mapper.apiToEntity(loanDto);
        try {
            loanEntity.setDevice(device);
            loanEntity.setEmployee(employee);
            device.setStatus(DeviceStatusEnum.PRESTADO);
            deviceRepository.save(device); 
            LoanEntity savedLoan = loanRepository.save(loanEntity);
            LOG.info("✅ Préstamo creado exitosamente. LoanID: {}, DeviceStatus actualizado a PRESTADO.", savedLoan.getId());
            return savedLoan;
        } catch (DataIntegrityViolationException ex) {
            LOG.error("Error de integridad DB al crear préstamo: {}", ex.getMessage());
            throw new InvalidInputException("No se pudo crear el préstamo. Verifique integridad de datos.");
        }
    }

    @Transactional
    public LoanEntity returnLoan(UUID loanId) {
        LOG.info("Procesando devolución de equipo para el préstamo ID: {}", loanId);
        LoanEntity loan = getLoanById(loanId);
        if (loan.getStatus() != LoanStatusEnum.ACTIVO) {
            LOG.warn("Intento de devolver un préstamo que no está activo. Estado actual: {}", loan.getStatus());
            throw new InvalidInputException("Este préstamo ya ha sido finalizado anteriormente.");
        }
        loan.setReturnedAt(LocalDateTime.now());
        loan.setStatus(LoanStatusEnum.DEVUELTO);
        DeviceEntity device = loan.getDevice();
        device.setStatus(DeviceStatusEnum.DISPONIBLE);
        deviceRepository.save(device);
        LoanEntity updatedLoan = loanRepository.save(loan);
        LOG.info(
            "✅ Devolución completada. Préstamo: DEVUELTO. Dispositivo {}: {} -> DISPONIBLE", 
            device.getName(),
            LoanStatusEnum.DEVUELTO.name()
        );
        return updatedLoan;
    }

    @Transactional
    public void deleteLoan(UUID id) {
        LOG.warn("Solicitud de eliminación forzada de préstamo ID: {}", id);
        if (!loanRepository.existsById(id)) {
            LOG.error("No se puede eliminar: Préstamo ID {} no existe.", id);
            throw new NotFoundException("No se puede eliminar. Préstamo no encontrado.");
        }
        loanRepository.deleteById(id);
        LOG.info("Préstamo eliminado ID: {}", id);
    }

    @Transactional
    public void deleteLoansBulk(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("La lista de IDs es requerida y no puede estar vacía");
        }
        LOG.info("Eliminando lote de {} préstamos.", ids.size());
        loanRepository.deleteAllById(ids);
    }
}
