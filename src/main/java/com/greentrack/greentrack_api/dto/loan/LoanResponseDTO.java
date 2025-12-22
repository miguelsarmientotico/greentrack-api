package com.greentrack.greentrack_api.dto.loan; 

import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoanResponseDTO {

    private UUID id;
    private LocalDateTime issuedAt;
    private LocalDateTime returnedAt;
    private LoanStatusEnum loanStatus;
    private EmployeeSummaryDTO employee;
    private DeviceSummaryDTO device;

    @Data
    public static class EmployeeSummaryDTO {
        private UUID id;
        private String username; 
        private String fullName; 
        private String email;
    }

    @Data
    public static class DeviceSummaryDTO {
        private UUID id;
        private String name;
        private String brand;
        private DeviceTypeEnum deviceType;
    }
}
