package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.dashboard.DashboardResponseDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import com.greentrack.greentrack_api.repository.LoanRepository;
import com.greentrack.greentrack_api.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboard() {
        List<DeviceEntity> allDevices = deviceRepository.findAll();
        List<LoanEntity> allLoans = loanRepository.findAll();
        long totalUsers = userRepository.count();

        LOG.debug(
            "Datos recuperados de BD -> Usuarios: {}, Dispositivos: {}, Préstamos: {}",
            totalUsers,
            allDevices.size(),
            allLoans.size()
        );
        long availableDevices = allDevices.stream()
        .filter(device -> DeviceStatusEnum.DISPONIBLE.equals(device.getStatus()))
        .count();

        long borrowedDevices = allDevices.stream()
        .filter(device -> DeviceStatusEnum.PRESTADO.equals(device.getStatus()))
        .count();

        long activeLoans = allLoans.stream()
        .filter(loan -> LoanStatusEnum.ACTIVO.equals(loan.getStatus()))
        .count();

        long returnedLoans = allLoans.stream()
        .filter(loan -> LoanStatusEnum.DEVUELTO.equals(loan.getStatus()))
        .count();

        LOG.info(
            "Métricas calculadas -> Dispositivos (Disp/Prest): {}/{}, Préstamos (Act/Dev): {}/{}", 
            availableDevices,
            borrowedDevices,
            activeLoans,
            returnedLoans
        );
        return DashboardResponseDTO.builder()
        .users(DashboardResponseDTO.UserStats.builder()
            .total(totalUsers)
            .build())
        .devices(DashboardResponseDTO.DeviceStats.builder()
            .total((long) allDevices.size())
            .available(availableDevices)
            .borrowed(borrowedDevices)
            .build())
        .loans(DashboardResponseDTO.LoanStats.builder()
            .total((long) allLoans.size())
            .active(activeLoans)
            .returned(returnedLoans)
            .build())
        .build();
    }
}
