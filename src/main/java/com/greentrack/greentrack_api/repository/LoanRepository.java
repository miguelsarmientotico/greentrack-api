package com.greentrack.greentrack_api.repository;

import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

    // 1. CORREGIDO: De userEntity -> employeeId
    // Usamos el campo UUID 'employeeId' que definimos en la entidad
    Page<LoanEntity> findByEmployeeId(UUID employeeId, Pageable pageable);

    // 2. CORREGIDO: De deviceEntity -> deviceId
    // Usamos el campo UUID 'deviceId'
    Page<LoanEntity> findByDeviceId(UUID deviceId, Pageable pageable);

    // 3. CORRECTO (No cambia)
    Page<LoanEntity> findByLoanStatus(LoanStatusEnum status, Pageable pageable);

    // 4. CORREGIDO: deviceEntity -> deviceId
    boolean existsByDeviceIdAndLoanStatus(UUID deviceId, LoanStatusEnum status);

    // 5. CORREGIDO: userEntity -> employeeId
    long countByEmployeeIdAndLoanStatus(UUID employeeId, LoanStatusEnum status);

    // 6. CORRECTO (No cambia)
    Page<LoanEntity> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 7. CORRECTO (No cambia)
    List<LoanEntity> findByLoanStatus(LoanStatusEnum status);
}
