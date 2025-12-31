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
  private UserEntity employee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID")
  private DeviceEntity device;

  @Column(name = "EMPLOYEE_ID", insertable = false, updatable = false)
  private UUID employeeId;

  @Column(name = "DEVICE_ID", insertable = false, updatable = false)
  private UUID deviceId;

  @NotNull(message = "Loan issue date is required.")
  @Column(name = "ISSUED_AT")
  private LocalDateTime issuedAt;

  @Column(name = "RETURNED_AT")
  private LocalDateTime returnedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "LOAN_STATUS")
  private LoanStatusEnum status = LoanStatusEnum.ACTIVO;

  @PrePersist
  public void prePersist() {
    if (this.issuedAt == null) {
      this.issuedAt = LocalDateTime.now();
    }
  }

  public UUID getId() {
    return id;
  }

  public LoanEntity setId(UUID id) {
    this.id = id;
    return this;
  }

  public UserEntity getEmployee() {
    return employee;
  }

  public LoanEntity setEmployee(UserEntity employee) {
    this.employee = employee;
    return this;
  }

  public DeviceEntity getDevice() {
    return device;
  }

  public LoanEntity setDevice(DeviceEntity device) {
    this.device = device;
    return this;
  }

  public UUID getEmployeeId() {
    return employeeId;
  }

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

  public LoanStatusEnum getStatus() {
    return status;
  }

  public LoanEntity setStatus(LoanStatusEnum status) {
    this.status = status;
    return this;
  }

  @Override
  public String toString() {
    return "LoanEntity{"
        + "id="
        + id
        + ", employeeId="
        + employeeId
        + ", deviceId="
        + deviceId
        + ", issuedAt="
        + issuedAt
        + ", returnedAt="
        + returnedAt
        + ", status="
        + status
        + '}';
  }
}
