package com.yarago.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User Data Transfer Object for User Management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String designation;
    private String department;
    private String employeeId;
    private Set<String> roles;
    private Long branchId;
    private String branchName;
    private Boolean active;
    private Boolean accountLocked;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
