package com.greentrack.greentrack_api.repository;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository
    extends JpaRepository<DeviceEntity, UUID>, JpaSpecificationExecutor<DeviceEntity> {
  boolean existsByName(String name);
}
