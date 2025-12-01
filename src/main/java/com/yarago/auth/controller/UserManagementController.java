package com.yarago.auth.controller;

import com.yarago.auth.dto.CreateUserRequest;
import com.yarago.auth.dto.UpdateUserRequest;
import com.yarago.auth.dto.UserDTO;
import com.yarago.auth.entity.Role;
import com.yarago.auth.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Management Controller (Admin only)
 * Handles CRUD operations for user management
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Admin endpoints for user management")
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * Get all users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Users", description = "Get list of all users (Admin only)")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDTO> users = userManagementService.getAllUsers();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", users);

        return ResponseEntity.ok(result);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get User by ID", description = "Get user details by ID (Admin only)")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        UserDTO user = userManagementService.getUserById(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", user);

        return ResponseEntity.ok(result);
    }

    /**
     * Create new user
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create User", description = "Create new user (Admin only)")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO user = userManagementService.createUser(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User created successfully");
        result.put("data", user);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update user
     */
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User", description = "Update user details (Admin only)")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO user = userManagementService.updateUser(userId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User updated successfully");
        result.put("data", user);

        return ResponseEntity.ok(result);
    }

    /**
     * Toggle user active status
     */
    @PatchMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle User Status", description = "Activate or deactivate user (Admin only)")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean active = request.get("active");
        if (active == null) {
            throw new IllegalArgumentException("Active status is required");
        }

        userManagementService.toggleUserStatus(userId, active);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", active ? "User activated successfully" : "User deactivated successfully");

        return ResponseEntity.ok(result);
    }

    /**
     * Unlock user account
     */
    @PostMapping("/users/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unlock User Account", description = "Unlock locked user account (Admin only)")
    public ResponseEntity<Map<String, Object>> unlockUserAccount(@PathVariable Long userId) {
        userManagementService.unlockUserAccount(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User account unlocked successfully");

        return ResponseEntity.ok(result);
    }

    /**
     * Reset user password
     */
    @PostMapping("/users/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset User Password", description = "Reset user password (Admin only)")
    public ResponseEntity<Map<String, Object>> resetUserPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        userManagementService.resetUserPassword(userId, newPassword);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Password reset successfully");

        return ResponseEntity.ok(result);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete User", description = "Delete user (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        userManagementService.deleteUser(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User deleted successfully");

        return ResponseEntity.ok(result);
    }

    /**
     * Get all roles
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Roles", description = "Get list of all available roles (Admin only)")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        List<Role> roles = userManagementService.getAllRoles();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", roles);

        return ResponseEntity.ok(result);
    }
}
