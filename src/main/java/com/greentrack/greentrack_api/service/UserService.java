package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.dto.UserDTO;
import com.greentrack.greentrack_api.dto.UserResponseDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import com.greentrack.greentrack_api.exception.InvalidInputException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.UserMapper;
import com.greentrack.greentrack_api.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public UserService(
        UserRepository repository,
        UserMapper mapper,
        @Lazy PasswordEncoder encoder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found..."));

        return new User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(user.getRole()) 
        );
    }

    public Page<UserEntity> getAllUsers(int page, int size, String fullNameFilter) {
        Pageable pageable = PageRequest.of(page, size);

        if (fullNameFilter != null && !fullNameFilter.isBlank()) {
            return repository.searchUsers(fullNameFilter, pageable);
        }
        return repository.findAll(pageable);
    }

    public Optional<UserResponseDTO> getUserById(UUID id) {
        return repository.findById(id).map(mapper::entityToApiResponse);
    }

    @Transactional
    public UserResponseDTO createUser(UserDTO userDTO) {
        if (repository.existsByUsername(userDTO.getUsername())) {
            throw new InvalidInputException("El nombre de usuario ya existe.");
        }
        if (repository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidInputException("El email ya está registrado.");
        }
        UserEntity userEntity = mapper.apiToEntity(userDTO);
        LOG.info("user creado tiene userStatus: {}", userEntity.getUserStatus());
        userEntity.setPassword(encoder.encode(userEntity.getPassword())); 
    
        try {
            UserEntity savedEntity = repository.save(userEntity);
            return mapper.entityToApiResponse(savedEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidInputException(
                "Could not create user."
            );
        }
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserDTO userUpdates) {
        UserEntity existingUser = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("No user found for userId: " + id));
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
            if (repository.existsByEmail(userUpdates.getEmail())) {
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
        if (userUpdates.getUserStatus() != null) {
            existingUser.setUserStatus(userUpdates.getUserStatus());
        }
        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isBlank()) {
            existingUser.setPassword(encoder.encode(userUpdates.getPassword()));
        }
        UserEntity updatedEntity = repository.save(existingUser);
        return mapper.entityToApiResponse(updatedEntity);
    }

    @Transactional
    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteUsersBulk(List<UUID> ids) {
        repository.deleteAllById(ids);
    }
}
