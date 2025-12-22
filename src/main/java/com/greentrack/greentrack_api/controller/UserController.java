package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.OnUpdate;
import com.greentrack.greentrack_api.dto.PagedResponse;
import com.greentrack.greentrack_api.dto.UserDTO;
import com.greentrack.greentrack_api.dto.UserResponseDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import com.greentrack.greentrack_api.exception.BadRequestException;
import com.greentrack.greentrack_api.exception.NotFoundException;
import com.greentrack.greentrack_api.mapper.UserMapper;
import com.greentrack.greentrack_api.service.UserService; 

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "${api.users.tag.description:Not Configured}")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper mapper;

    public UserController(
        UserService userService,
        UserMapper mapper
    ) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(
        summary = "${api.users.get-all.summary:Not Configured}",
        description = "${api.users.get-all.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponseDTO>> getAllUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) String fullNameFilter
    ) {
        Page<UserEntity> usersPage = userService.getAllUsers(page - 1, limit, fullNameFilter);
        LOG.info("usersPage: {}", usersPage.toString());

        List<UserResponseDTO> userDTOs = usersPage.getContent().stream()
        .map(mapper::entityToApiResponse)
        .toList();

        PagedResponse<UserResponseDTO> response = new PagedResponse<>(
            userDTOs,
            usersPage.getNumber() + 1,
            usersPage.getSize(),            
            usersPage.getTotalElements(),   
            usersPage.getTotalPages(),      
            usersPage.isLast()              
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "${api.users.get-by-id.summary:Not Configured}",
        description = "${api.users.get-by-id.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID userId) {
        UserResponseDTO user = userService.getUserById(userId)
        .orElseThrow(() -> new NotFoundException("No user found for userId: " + userId));
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "${api.users.create.summary:Not Configured}",
        description = "${api.users.create.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.responseCodes.created.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
        UserResponseDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(
        summary = "${api.users.update.summary:Not Configured}",
        description = "${api.users.update.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId, @Validated(OnUpdate.class) @RequestBody UserDTO userDTO) {
        UserResponseDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "${api.users.delete.summary:Not Configured}",
        description = "${api.users.delete.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "${api.responseCodes.noContent.description:Not Configured}"),
        @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "${api.users.bulk-delete.summary:Not Configured}",
        description = "${api.users.bulk-delete.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteUsersBulk(@RequestBody Map<String, List<UUID>> payload) {
        List<UUID> ids = payload.get("ids");
        if (ids == null) {
            throw new BadRequestException("El campo 'ids' es requerido");
        }

        if (ids.isEmpty()) {
            throw new BadRequestException("La lista de IDs no puede estar vacía");
        }
        userService.deleteUsersBulk(ids);
        return ResponseEntity.ok().build();
    }
}
