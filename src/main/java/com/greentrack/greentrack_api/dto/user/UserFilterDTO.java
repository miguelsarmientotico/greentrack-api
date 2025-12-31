package com.greentrack.greentrack_api.dto.user;

import com.greentrack.greentrack_api.entity.RoleEnum;
import com.greentrack.greentrack_api.entity.UserStatusEnum;

public record UserFilterDTO(
    String id,
    String username,
    String fullName,
    String email,
    UserStatusEnum status,
    RoleEnum role,
    String globalSearch) {}
