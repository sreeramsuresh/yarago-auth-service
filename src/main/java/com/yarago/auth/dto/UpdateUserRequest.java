package com.yarago.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for updating an existing user (Admin only)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String designation;
    private String department;
    private String employeeId;
    private Set<String> roles;
    private Long branchId;
    private Boolean active;
}
