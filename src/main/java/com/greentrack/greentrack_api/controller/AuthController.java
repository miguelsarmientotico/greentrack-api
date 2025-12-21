package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.dto.AuthDTO;
import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.dto.UserDTO;
import com.greentrack.greentrack_api.entity.UserEntity;
import com.greentrack.greentrack_api.service.JwtService;
import com.greentrack.greentrack_api.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController (
        UserService userService,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        LOG.info("Intento de logeo: {}/{}", authDTO.getUsername(), authDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authDTO.getUsername());
            return ResponseEntity.ok(Map.of(
                "accessToken", token
            ));
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
        // Lógica: Guardar usuario con contraseña encriptada
        // return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
        //return ResponseEntity.status(HttpStatus.CREATED); // Mock
        return null;
    }

    // DTO simple para el Login
    public record LoginRequest(String username, String password) {}
}
