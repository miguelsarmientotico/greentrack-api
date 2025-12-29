package com.greentrack.greentrack_api.dto.device;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.OnUpdate;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceDTO {

    @NotBlank(groups = OnCreate.class, message = "El nombre es obligatorio")
    @Size(min = 1, groups = OnUpdate.class, message = "El nombre no puede estar vacío")
    private String name;

    @NotNull(groups = OnCreate.class, message = "El tipo de dispositivo es obligatorio")
    private DeviceTypeEnum type;

    @NotBlank(groups = OnCreate.class, message = "La marca es obligatoria")
    @Size(min = 1, groups = OnUpdate.class, message = "La marca no puede estar vacía")
    private String brand;

    @Null(groups = OnCreate.class, message = "El estado se asigna automáticamente")
    private DeviceStatusEnum status;
}
