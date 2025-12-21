package com.greentrack.greentrack_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"loan\"")
public class LoanEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    // --- RELACIONES (Para escribir y leer objetos) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
    private UserEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID")
    private DeviceEntity device;

    // --- IDS READ-ONLY (Solo para leer IDs rápido) ---
    @Column(name = "EMPLOYEE_ID", insertable = false, updatable = false)
    private UUID employeeId;

    @Column(name = "DEVICE_ID", insertable = false, updatable = false)
    private UUID deviceId;

    // --- DATOS ---
    @NotNull(message = "Loan issue date is required.")
    @Column(name = "ISSUED_AT")
    private LocalDateTime issuedAt;

    @Column(name = "RETURNED_AT")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAN_STATUS")
    private LoanStatusEnum loanStatus = LoanStatusEnum.ACTIVO;

    @PrePersist
    public void prePersist() {
        if (this.issuedAt == null) {
            this.issuedAt = LocalDateTime.now();
        }
    }

    // --- GETTERS Y SETTERS ---

    public UUID getId() {
        return id;
    }

    public LoanEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    // 1. Corregido: El nombre del método coincide con la variable 'employee'
    public UserEntity getEmployee() {
        return employee;
    }

    public LoanEntity setEmployee(UserEntity employee) {
        this.employee = employee;
        return this;
    }

    // 2. Corregido: El nombre del método coincide con la variable 'device'
    public DeviceEntity getDevice() {
        return device;
    }

    public LoanEntity setDevice(DeviceEntity device) {
        this.device = device;
        return this;
    }

    // 3. AÑADIDO: Getter para employeeId (Sin Setter)
    public UUID getEmployeeId() {
        return employeeId;
    }

    // 4. AÑADIDO: Getter para deviceId (Sin Setter)
    public UUID getDeviceId() {
        return deviceId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LoanEntity setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
        return this;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public LoanEntity setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
        return this;
    }

    public LoanStatusEnum getLoanStatus() {
        return loanStatus;
    }

    public LoanEntity setLoanStatus(LoanStatusEnum loanStatus) {
        this.loanStatus = loanStatus;
        return this;
    }

    @Override
    public String toString() {
        return "LoanEntity{"
        + "id=" + id
        + ", employeeId=" + employeeId // Ahora es seguro imprimir esto sin romper lazy loading
        + ", deviceId=" + deviceId     // Idem
        + ", issuedAt=" + issuedAt
        + ", returnedAt=" + returnedAt
        + ", loanStatus=" + loanStatus
        + '}';
    }
}
