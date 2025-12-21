package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.entity.*;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.LoanRepository;
import com.greentrack.greentrack_api.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public LoanService(LoanRepository loanRepository, DeviceRepository deviceRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
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
            return loanRepository.findByUserEntity_Id(employeeId, PageRequest.of(0, 100)).getContent();
        }
        return loanRepository.findAll();
    }

    @Transactional
    public LoanEntity createLoan(UUID userId, UUID deviceId) {
        // 1. Validar que el dispositivo exista
        DeviceEntity device = deviceRepository.findById(deviceId)
        .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        // 2. Validar que el usuario exista
        UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. REGLA DE NEGOCIO: ¿El dispositivo ya está en uso?
        boolean isBusy = loanRepository.existsByDeviceEntity_IdAndLoanStatus(deviceId, LoanStatusEnum.ACTIVO);
        if (isBusy) {
            throw new IllegalArgumentException("El dispositivo seleccionado ya se encuentra en un préstamo activo.");
        }

        // 4. REGLA DE NEGOCIO: Validar que el estado del device sea DISPONIBLE
        if (device.getDeviceStatus() != DeviceStatusEnum.DISPONIBLE) {
            throw new IllegalArgumentException("El dispositivo no está marcado como DISPONIBLE en el inventario.");
        }

        // 5. Crear el Préstamo
        LoanEntity loan = new LoanEntity();
        loan.setUserEntity(user);
        loan.setDeviceEntity(device);
        loan.setIssuedAt(LocalDateTime.now());
        loan.setLoanStatus(LoanStatusEnum.ACTIVO);

        // 6. ACTUALIZAR EL ESTADO DEL DISPOSITIVO A "OCUPADO" (o ASIGNADO)
        // Asumiremos que tienes un estado ASIGNADO o lo dejas como NO DISPONIBLE
        device.setDeviceStatus(DeviceStatusEnum.PRESTADO); // Asegúrate de tener este ENUM, si no usa otro.
        deviceRepository.save(device);

        return loanRepository.save(loan);
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
        DeviceEntity device = loan.getDeviceEntity();
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
