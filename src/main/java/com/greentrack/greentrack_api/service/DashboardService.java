package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.DashboardResponseDTO;
import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.mapper.DeviceMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.LoanRepository;
import com.greentrack.greentrack_api.repository.UserRepository;

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
public class DashboardService {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardService.class);

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final LoanRepository loanRepository;

    public DashboardService(
        UserRepository userRepository,
        DeviceRepository deviceRepository,
        LoanRepository loanRepository
    ) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.loanRepository = loanRepository;
    }

    public DashboardResponseDTO getDashboard() {
        long totalDevices = deviceRepository.count();
        long availableDevices = deviceRepository.countByDeviceStatus(DeviceStatusEnum.DISPONIBLE);
        long borrowedDevices = deviceRepository.countByDeviceStatus(DeviceStatusEnum.PRESTADO);

        // 2. Obtener estadísticas de Préstamos
        long totalLoans = loanRepository.count();
        long activeLoans = loanRepository.countByLoanStatus(LoanStatusEnum.ACTIVO);
        long returnedLoans = loanRepository.countByLoanStatus(LoanStatusEnum.DEVUELTO); // O countByReturnedAtIsNotNull()

        // 3. Obtener estadísticas de Usuarios
        long totalUsers = userRepository.count();

        // 4. Construir y devolver la respuesta anidada
        return DashboardResponseDTO.builder()
        .users(DashboardResponseDTO.UserStats.builder()
            .total(totalUsers)
            .build())
        .devices(DashboardResponseDTO.DeviceStats.builder()
            .total(totalDevices)
            .available(availableDevices)
            .borrowed(borrowedDevices)
            .build())
        .loans(DashboardResponseDTO.LoanStats.builder()
            .total(totalLoans)
            .active(activeLoans)
            .returned(returnedLoans)
            .build())
        .build();
    }
}
