package com.greentrack.greentrack_api.mapper;

import com.greentrack.greentrack_api.dto.loan.LoanDTO;
import com.greentrack.greentrack_api.dto.loan.LoanResponseDTO;
import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.LoanEntity;
import com.greentrack.greentrack_api.entity.UserEntity;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface LoanMapper {

  LoanDTO entityToApi(LoanEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "issuedAt", ignore = true)
  @Mapping(target = "returnedAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "employee", ignore = true)
  @Mapping(target = "device", ignore = true)
  @Mapping(target = "employeeId", ignore = true)
  @Mapping(target = "deviceId", ignore = true)
  LoanEntity apiToEntity(LoanDTO api);

  List<LoanDTO> entityListToApiList(List<LoanEntity> entity);

  List<LoanEntity> apiListToEntityList(List<LoanDTO> api);

  LoanResponseDTO entityToApiResponse(LoanEntity entity);

  LoanResponseDTO.EmployeeSummaryDTO userToSummary(UserEntity user);

  LoanResponseDTO.DeviceSummaryDTO deviceToSummary(DeviceEntity device);

  List<LoanResponseDTO> entityListToApiResponseList(List<LoanEntity> entity);
}
