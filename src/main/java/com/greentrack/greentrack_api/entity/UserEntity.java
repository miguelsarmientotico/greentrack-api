package com.greentrack.greentrack_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User name is required.")
    @Basic(optional = false)
    @Column(name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_STATUS")
    private UserStatusEnum userStatus = UserStatusEnum.ACTIVO;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.USER;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LoanEntity> loans;

    public UUID getId() {
        return id;
    }

    public UserEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) { 
        this.password = password;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public UserEntity setFullName(String fullName) { 
        this.fullName = fullName;
        return this;
    }

    public String getEmail() {
        return email; 
    }

    public UserEntity setEmail(String email) { 
        this.email = email; 
        return this;
    }

    public UserStatusEnum getUserStatus() {
        return userStatus;
    }

    public UserEntity setUserStatus(UserStatusEnum userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public RoleEnum getRole() {
        return role;
    }

    public UserEntity setRole(RoleEnum role) {
        this.role = role;
        return this;
    }

    public List<LoanEntity> getLoans() {
        return loans;
    }

    public UserEntity setLoans(List<LoanEntity> loans) {
        this.loans = loans;
        return this;
    }
}
