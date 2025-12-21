package com.greentrack.greentrack_api.dto.loan;

import com.greentrack.greentrack_api.dto.OnCreate;
import com.greentrack.greentrack_api.entity.LoanStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoanDTO {

    // --- CAMPOS OBLIGATORIOS (Entrada) ---

    @NotNull(groups = OnCreate.class, message = "El ID del empleado es obligatorio")
    private UUID employeeId;

    @NotNull(groups = OnCreate.class, message = "El ID del dispositivo es obligatorio")
    private UUID deviceId;

    // --- CAMPOS BLOQUEADOS (Seguridad) ---
    // Se fuerzan a ser nulos en la creación para asegurar que el Backend
    // sea el único responsable de asignar fechas y estados.

    @Null(groups = OnCreate.class, message = "La fecha de inicio se asigna automáticamente, no la envíe.")
    private LocalDateTime issuedAt;

    @Null(groups = OnCreate.class, message = "La fecha de devolución no se envía al crear.")
    private LocalDateTime returnedAt;

    @Null(groups = OnCreate.class, message = "El estado se asigna automáticamente (ACTIVO).")
    private LoanStatusEnum loanStatus;
}
