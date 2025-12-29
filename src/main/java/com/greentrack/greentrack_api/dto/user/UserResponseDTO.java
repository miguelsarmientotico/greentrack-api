package com.greentrack.greentrack_api.dto.user;

import java.util.UUID;

import com.greentrack.greentrack_api.entity.RoleEnum;
import com.greentrack.greentrack_api.entity.UserStatusEnum;

import lombok.Data;

@Data
public class UserResponseDTO {

    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private UserStatusEnum status;
    private RoleEnum role;
}

