package com.greentrack.greentrack_api.dto.device;

import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;

public record DeviceFilterDTO(
    String id,
    String name,
    String brand,
    DeviceTypeEnum type,
    DeviceStatusEnum status,
    String globalSearch) {}
