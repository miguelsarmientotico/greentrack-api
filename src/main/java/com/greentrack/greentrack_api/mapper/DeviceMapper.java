package com.greentrack.greentrack_api.mapper;

import java.util.List;

import org.mapstruct.Builder; // <--- 1. Tienes que importar esto
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.greentrack.greentrack_api.dto.device.DeviceDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;

@Mapper(
    componentModel = "spring", 
    builder = @Builder(disableBuilder = true)
)
public interface DeviceMapper {

    DeviceDTO entityToApi(DeviceEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deviceStatus", ignore = true)
    DeviceEntity apiToEntity(DeviceDTO api);

    List<DeviceDTO> entityListToApiList(List<DeviceEntity> entity);

    List<DeviceEntity> apiListToEntityList(List<DeviceDTO> api);
}
