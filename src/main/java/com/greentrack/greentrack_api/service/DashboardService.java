package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.DashboardResponseDTO;
import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.entity.LoanEntity;
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
        List<DeviceEntity> allDevices = deviceRepository.findAll();
        List<LoanEntity> allLoans = loanRepository.findAll();
        long totalUsers = userRepository.count(); // Este lo puedes dejar así o hacer findAll().stream().count()

        long availableDevices = allDevices.stream()
        .filter(device -> DeviceStatusEnum.DISPONIBLE.equals(device.getDeviceStatus()))
        .count();

        long borrowedDevices = allDevices.stream()
        .filter(device -> DeviceStatusEnum.PRESTADO.equals(device.getDeviceStatus()))
        .count();

        long activeLoans = allLoans.stream()
        .filter(loan -> LoanStatusEnum.ACTIVO.equals(loan.getLoanStatus()))
        .count();

        long returnedLoans = allLoans.stream()
        .filter(loan -> LoanStatusEnum.DEVUELTO.equals(loan.getLoanStatus()))
        .count();

        return DashboardResponseDTO.builder()
        .users(DashboardResponseDTO.UserStats.builder()
            .total(totalUsers)
            .build())
        .devices(DashboardResponseDTO.DeviceStats.builder()
            .total((long) allDevices.size()) // Size de la lista es el total
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
