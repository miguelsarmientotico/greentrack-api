package com.greentrack.greentrack_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"device\"")
public class DeviceEntity {
  @Id
  @GeneratedValue
  @Column(name = "ID", updatable = false, nullable = false)
  private UUID id;

  @NotNull(message = "Device name is required.")
  @Basic(optional = false)
  @Column(name = "NAME", unique = true)
  private String name;

  @NotNull(message = "Device type is required.")
  @Enumerated(EnumType.STRING)
  @Column(name = "DEVICE_TYPE")
  private DeviceTypeEnum type;

  @NotNull(message = "Device brand is required.")
  @Basic(optional = false)
  @Column(name = "BRAND")
  private String brand;

  @Enumerated(EnumType.STRING)
  @Column(name = "DEVICE_STATUS")
  private DeviceStatusEnum status = DeviceStatusEnum.DISPONIBLE;

  @OneToMany(mappedBy = "device", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<LoanEntity> loans;

  public UUID getId() {
    return id;
  }

  public DeviceEntity setId(UUID id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public DeviceEntity setName(String name) {
    this.name = name;
    return this;
  }

  public DeviceTypeEnum getType() {
    return type;
  }

  public DeviceEntity setType(DeviceTypeEnum type) {
    this.type = type;
    return this;
  }

  public String getBrand() {
    return brand;
  }

  public DeviceEntity setBrand(String brand) {
    this.brand = brand;
    return this;
  }

  public DeviceStatusEnum getStatus() {
    return status;
  }

  public DeviceEntity setStatus(DeviceStatusEnum status) {
    this.status = status;
    return this;
  }

  @Override
  public String toString() {
    return "DeviceEntity{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", brand='"
        + brand
        + '\''
        + ", status='"
        + status
        + '}';
  }
}
