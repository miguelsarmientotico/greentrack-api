package com.greentrack.greentrack_api.repository;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID> {

    Page<DeviceEntity> findByDeviceStatus(DeviceStatusEnum status, Pageable pageable);

    Page<DeviceEntity> findByDeviceType(DeviceTypeEnum type, Pageable pageable);

    Page<DeviceEntity> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
            String name, 
            String brand, 
            Pageable pageable
    );

    Page<DeviceEntity> findByDeviceTypeAndDeviceStatus(
            DeviceTypeEnum type, 
            DeviceStatusEnum status, 
            Pageable pageable
    );
    
    boolean existsByName(String name);
}
