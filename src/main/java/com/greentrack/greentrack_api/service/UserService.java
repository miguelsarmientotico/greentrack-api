package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.user.UserDTO;
import com.greentrack.greentrack_api.dto.user.UserFilterDTO;
import com.greentrack.greentrack_api.dto.user.UserResponseDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import com.greentrack.greentrack_api.exception.BadRequestException;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.UserMapper;
import com.greentrack.greentrack_api.repository.UserRepository;
import com.greentrack.greentrack_api.repository.specifications.UserSpecifications;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository repository;
  private final PasswordEncoder encoder;
  private final UserMapper mapper;

  public UserService(UserRepository repository, UserMapper mapper, @Lazy PasswordEncoder encoder) {
    this.repository = repository;
    this.mapper = mapper;
    this.encoder = encoder;
  }

  private static final List<String> CAMPOS_VALIDOS =
      List.of("id", "username", "fullName", "email", "status", "role");

  @Transactional(readOnly = true)
  public Page<UserEntity> searchUsersAdvanced(UserFilterDTO filter, Pageable pageable) {
    if (pageable.getSort().isSorted()) {
      for (Sort.Order order : pageable.getSort()) {
        if (!CAMPOS_VALIDOS.contains(order.getProperty())) {
          throw new InvalidInputException(
              "Campo de ordenamiento no válido: " + order.getProperty());
        }
      }
    }
    Specification<UserEntity> spec = UserSpecifications.getUsers(filter);
    return repository.findAll(spec, pageable);
  }

  @Transactional(readOnly = true)
  public UserResponseDTO getUserById(UUID id) {
    return repository
        .findById(id)
        .map(mapper::entityToApiResponse)
        .orElseThrow(
            () -> {
              LOG.warn("Intento de acceso a usuario inexistente ID: {}", id);
              return new NotFoundException("No user found for userId: " + id);
            });
  }

  @Transactional
  public UserResponseDTO createUser(UserDTO userDTO) {
    LOG.info("Intentando registrar nuevo usuario: {}", userDTO.getUsername());
    if (repository.existsByUsername(userDTO.getUsername())) {
      LOG.warn("Registro fallido: Username '{}' ya existe.", userDTO.getUsername());
      throw new InvalidInputException("El nombre de usuario ya existe.");
    }
    if (repository.existsByEmail(userDTO.getEmail())) {
      LOG.warn("Registro fallido: Email '{}' ya ocupado.", userDTO.getEmail());
      throw new InvalidInputException("El email ya está registrado.");
    }
    UserEntity userEntity = mapper.apiToEntity(userDTO);
    userEntity.setPassword(encoder.encode(userEntity.getPassword()));
    try {
      UserEntity savedEntity = repository.save(userEntity);
      LOG.info(
          "✅ Usuario creado exitosamente ID: {} | Rol: {} | Status: {}",
          savedEntity.getId(),
          savedEntity.getRole(),
          savedEntity.getStatus());
      return mapper.entityToApiResponse(savedEntity);
    } catch (DataIntegrityViolationException ex) {
      LOG.error("Error crítico de DB al crear usuario: {}", ex.getMessage());
      throw new InvalidInputException("Could not create user. Integrity violation.");
    }
  }

  @Transactional
  public UserResponseDTO updateUser(UUID id, UserDTO userUpdates) {
    LOG.info("Iniciando actualización para usuario ID: {}", id);
    UserEntity existingUser =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("No user found for userId: " + id));
    if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
      if (repository.existsByEmail(userUpdates.getEmail())) {
        LOG.warn(
            "Actualización fallida: Email '{}' ya pertenece a otro usuario.",
            userUpdates.getEmail());
        throw new InvalidInputException("Email ya registrado");
      }
    }
    if (userUpdates.getFullName() != null) {
      existingUser.setFullName(userUpdates.getFullName());
    }
    if (userUpdates.getEmail() != null) {
      existingUser.setEmail(userUpdates.getEmail());
    }
    if (userUpdates.getRole() != null) {
      existingUser.setRole(userUpdates.getRole());
    }
    if (userUpdates.getStatus() != null) {
      existingUser.setStatus(userUpdates.getStatus());
    }
    if (userUpdates.getPassword() != null && !userUpdates.getPassword().isBlank()) {
      existingUser.setPassword(encoder.encode(userUpdates.getPassword()));
    }
    UserEntity updatedEntity = repository.save(existingUser);
    return mapper.entityToApiResponse(updatedEntity);
  }

  @Transactional
  public void deleteUser(UUID id) {
    LOG.warn("Solicitud de eliminación de usuario ID: {}", id);
    repository.deleteById(id);
    LOG.info("Usuario eliminado correctamente ID: {}", id);
  }

  @Transactional
  public void deleteUsersBulk(List<UUID> ids) {
    if (ids == null) {
      throw new BadRequestException("El campo 'ids' es requerido");
    }
    if (ids.isEmpty()) {
      throw new BadRequestException("La lista de IDs no puede estar vacía");
    }
    LOG.info("Eliminación masiva solicitada para {} usuarios.", ids.size());
    repository.deleteAllById(ids);
  }
}
