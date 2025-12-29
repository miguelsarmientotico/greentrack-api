package com.greentrack.greentrack_api.dto.loan;

import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import com.greentrack.greentrack_api.entity.RoleEnum;
import com.greentrack.greentrack_api.entity.UserStatusEnum;
import java.time.LocalDateTime;

public record LoanFilterDTO(
    String id,
    
    // Employee Params
    String employeeId,
    String employeeUsername,
    String employeeFullName,
    String employeeEmail,
    UserStatusEnum employeeStatus,
    RoleEnum employeeRole,
    
    // Device Params
    String deviceId,
    String deviceName,
    String deviceBrand,
    DeviceTypeEnum deviceType,
    DeviceStatusEnum deviceStatus,
    
    // Loan Params
    LocalDateTime issuedAt,
    LocalDateTime startIssuedAt,
    LocalDateTime endIssuedAt,
    LocalDateTime returnedAt,
    LocalDateTime startReturnedAt,
    LocalDateTime endReturnedAt,
    LoanStatusEnum loanStatus,
    
    String globalSearch
) {}
