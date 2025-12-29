package com.greentrack.greentrack_api.mapper;

import java.util.List;

import org.mapstruct.Builder; 
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.greentrack.greentrack_api.dto.user.UserDTO;
import com.greentrack.greentrack_api.dto.user.UserResponseDTO;
import com.greentrack.greentrack_api.entity.UserEntity;

@Mapper(
    componentModel = "spring", 
    builder = @Builder(disableBuilder = true)
)
public interface UserMapper {

    UserDTO entityToApi(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loans", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserEntity apiToEntity(UserDTO api);

    List<UserDTO> entityListToApiList(List<UserEntity> entity);

    List<UserEntity> apiListToEntityList(List<UserDTO> api);

    UserResponseDTO entityToApiResponse(UserEntity entity);
      
    List<UserResponseDTO> entityListToResponseDtoList(List<UserEntity> entities);

    UserResponseDTO dtoToResponseDto(UserDTO dto);
    
    List<UserResponseDTO> dtoListToResponseDtoList(List<UserDTO> dtos);

}
