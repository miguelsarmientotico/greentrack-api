package com.greentrack.greentrack_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AuthDTO {
    @NotBlank(message = "El username es obligatorio")
    private String username;
    @NotBlank(message = "El password es obligatorio")
    private String password;
}
