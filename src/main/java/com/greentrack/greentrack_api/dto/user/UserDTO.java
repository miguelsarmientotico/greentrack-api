package com.greentrack.greentrack_api.dto.user;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.OnUpdate;
import com.greentrack.greentrack_api.entity.RoleEnum;
import com.greentrack.greentrack_api.entity.UserStatusEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank(groups = OnCreate.class, message = "El username es obligatorio")
    @Null(groups = OnUpdate.class, message = "No se permite modificar el username")
    @Size(min = 4, max = 20, groups = OnCreate.class)
    private String username;

    @NotBlank(groups = OnCreate.class, message = "La contraseña es obligatoria al crear")
    @Size(min = 8, groups = {OnCreate.class, OnUpdate.class}, message = "Mínimo 8 caracteres")
    private String password;

    @NotBlank(groups = OnCreate.class, message = "El nombre completo es obligatorio")
    private String fullName;

    @NotBlank(groups = OnCreate.class, message = "El email es obligatorio")
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Formato de email inválido")
    private String email;

    @Null(groups = OnCreate.class, message = "El estado se asigna automáticamente, no lo envíe")
    private UserStatusEnum status;

    @NotNull(groups = OnCreate.class, message = "El rol es obligatorio")
    private RoleEnum role;
}
