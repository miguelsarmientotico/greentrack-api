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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID")
    private DeviceEntity deviceEntity;

    @NotNull(message = "Loan issue date is required.")
    @Column(name = "ISSUED_AT") // Corregí el posible typo en el nombre de la columna
    private LocalDateTime issuedAt;

    @Column(name = "RETURNED_AT") // Corregí el posible typo en el nombre de la columna
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAN_STATUS")
    private LoanStatusEnum loanStatus = LoanStatusEnum.ACTIVO;

    public UUID getId() {
        return id;
    }

    public LoanEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public LoanEntity setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        return this;
    }

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public LoanEntity setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
        return this;
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
        + "id="
        + id
        + '\''
        + ", issuedAt='"
        + issuedAt
        + '\''
        + ", returnedAt='"
        + returnedAt
        + '\''
        + ", loanStatus='"
        + loanStatus
        + '}';
    }
}

