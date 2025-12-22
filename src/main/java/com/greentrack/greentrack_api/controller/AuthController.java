package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.AuthDTO;
import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.UserDTO;
import com.greentrack.greentrack_api.dto.UserResponseDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import com.greentrack.greentrack_api.repository.UserRepository;
import com.greentrack.greentrack_api.service.JwtService;
import com.greentrack.greentrack_api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "${api.auth.tag.description:Not Configured}")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController (
        UserRepository userRepository,
        UserService userService,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(
        summary = "${api.auth.login.summary:Not Configured}",
        description = "${api.auth.login.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description:Not Configured}"),
        @ApiResponse(responseCode = "401", description = "${api.responseCodes.unauthorized.description:Not Configured}")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        LOG.info("Intento de logeo: {}/{}", authDTO.getUsername(), authDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            UserEntity user = userRepository.findByUsername(authDTO.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado a pesar de autenticarse"));
            String token = jwtService.generateToken(
                authDTO.getUsername(),
                user.getRole().name()
            );
            return ResponseEntity.ok(Map.of(
                "accessToken", token
            ));
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @Operation(
        summary = "${api.auth.register.summary:Not Configured}",
        description = "${api.auth.register.description:Not Configured}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "${api.responseCodes.created.description:Not Configured}"),
        @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description:Not Configured}"),
        @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description:Not Configured}")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
        UserResponseDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
