package com.greentrack.greentrack_api.repository;

import com.greentrack.greentrack_api.entity.LoanEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository
    extends JpaRepository<LoanEntity, UUID>, JpaSpecificationExecutor<LoanEntity> {}
