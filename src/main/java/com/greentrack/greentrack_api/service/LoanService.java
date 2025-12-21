package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.loan.LoanDTO;
import com.greentrack.greentrack_api.entity.*;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.mapper.LoanMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.LoanRepository;
import com.greentrack.greentrack_api.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public Page<LoanEntity> getAllLoans(int page, int size) {
        return loanRepository.findAll(PageRequest.of(page, size));
    }

    // Filtro simple para historial
    public List<LoanEntity> filterLoans(UUID employeeId, LocalDate dateFrom, LocalDate dateTo) {
        // Nota: Para simplificar, si viene ID de empleado filtramos por él.
        // Una implementación más robusta usaría CriteriaBuilder o Specifications para rangos de fecha.
        if (employeeId != null) {
            // Truco: usamos Pageable unpaged para traer lista completa si es necesario, 
            // o podrías crear un método List<Loan> en el repo.
            return loanRepository.findByEmployeeId(employeeId, PageRequest.of(0, 100)).getContent();
        }
        return loanRepository.findAll();
    }

    @Transactional
    public LoanEntity createLoan(LoanDTO loanDto) {
        LoanEntity loanEntity = mapper.apiToEntity(loanDto);
        DeviceEntity device = deviceRepository.findById(loanDto.getDeviceId())
            .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        if (device.getDeviceStatus() != DeviceStatusEnum.DISPONIBLE) {
            throw new RuntimeException("El dispositivo no está disponible. Estado actual: " + device.getDeviceStatus());
        }

        UserEntity employee = userRepository.findById(loanDto.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        try {
            loanEntity.setDevice(device);
            loanEntity.setEmployee(employee);
            device.setDeviceStatus(DeviceStatusEnum.PRESTADO);
            deviceRepository.save(device); 
            return loanRepository.save(loanEntity);

        } catch (DataIntegrityViolationException ex) {
            throw new InvalidInputException("No se pudo crear el préstamo. Verifique que los datos sean correctos.");
        }
    }

    @Transactional
    public LoanEntity returnLoan(UUID loanId) {
        // 1. Buscar el préstamo
        LoanEntity loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // 2. Validar que no esté ya devuelto
        if (loan.getLoanStatus() != LoanStatusEnum.ACTIVO) {
            throw new IllegalArgumentException("Este préstamo ya ha sido finalizado anteriormente.");
        }

        // 3. Finalizar el préstamo
        loan.setReturnedAt(LocalDateTime.now());
        loan.setLoanStatus(LoanStatusEnum.DEVUELTO);

        // 4. LIBERAR EL DISPOSITIVO (Volver a DISPONIBLE)
        DeviceEntity device = loan.getDevice();
        device.setDeviceStatus(DeviceStatusEnum.DISPONIBLE);
        deviceRepository.save(device);

        return loanRepository.save(loan);
    }

    public Optional<LoanEntity> getLoanById(UUID id) {
        return loanRepository.findById(id);
    }

    @Transactional
    public void deleteLoan(UUID id) {
        loanRepository.deleteById(id);
    }

    @Transactional
    public void deleteLoansBulk(List<UUID> ids) {
        loanRepository.deleteAllById(ids);
    }
}
