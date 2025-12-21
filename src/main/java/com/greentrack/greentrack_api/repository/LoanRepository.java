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

    Page<LoanEntity> findByEmployeeId(UUID employeeId, Pageable pageable);

    Page<LoanEntity> findByDeviceId(UUID deviceId, Pageable pageable);

    Page<LoanEntity> findByLoanStatus(LoanStatusEnum status, Pageable pageable);

    boolean existsByDeviceIdAndLoanStatus(UUID deviceId, LoanStatusEnum status);

    long countByEmployeeIdAndLoanStatus(UUID employeeId, LoanStatusEnum status);

    Page<LoanEntity> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<LoanEntity> findByLoanStatus(LoanStatusEnum status);

    long countByLoanStatus(LoanStatusEnum status);
}
