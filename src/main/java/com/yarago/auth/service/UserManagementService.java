package com.yarago.auth.service;

import com.yarago.auth.dto.CreateUserRequest;
import com.yarago.auth.dto.UpdateUserRequest;
import com.yarago.auth.dto.UserDTO;
import com.yarago.auth.entity.Role;
import com.yarago.auth.entity.User;
import com.yarago.auth.repository.RoleRepository;
import com.yarago.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for User Management operations (Admin only)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toUserDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return toUserDTO(user);
    }

    /**
     * Create new user
     */
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDesignation(request.getDesignation());
        user.setDepartment(request.getDepartment());
        user.setEmployeeId(request.getEmployeeId());
        user.setBranchId(request.getBranchId());
        user.setActive(request.getActive() != null ? request.getActive() : true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setPasswordChangedAt(LocalDateTime.now());

        // Assign roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        } else {
            // Default role: RECEPTIONIST
            Role defaultRole = roleRepository.findByName("ROLE_RECEPTIONIST")
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
            user.getRoles().add(defaultRole);
        }

        user = userRepository.save(user);
        log.info("User created: {}", user.getUsername());

        return toUserDTO(user);
    }

    /**
     * Update user
     */
    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Update email if changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDesignation() != null) {
            user.setDesignation(request.getDesignation());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getEmployeeId() != null) {
            user.setEmployeeId(request.getEmployeeId());
        }
        if (request.getBranchId() != null) {
            user.setBranchId(request.getBranchId());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // Update roles if provided
        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        log.info("User updated: {}", user.getUsername());

        return toUserDTO(user);
    }

    /**
     * Toggle user active status
     */
    @Transactional
    public void toggleUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setActive(active);

        // If deactivating, also unlock account
        if (!active) {
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);
        log.info("User {} status changed to: {}", user.getUsername(), active ? "active" : "inactive");
    }

    /**
     * Unlock user account
     */
    @Transactional
    public void unlockUserAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        log.info("User {} account unlocked", user.getUsername());
    }

    /**
     * Reset user password (Admin)
     */
    @Transactional
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        log.info("Password reset for user: {}", user.getUsername());
    }

    /**
     * Delete user
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        userRepository.delete(user);
        log.info("User deleted: {}", user.getUsername());
    }

    /**
     * Get all roles
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phoneNumber(user.getPhoneNumber())
            .designation(user.getDesignation())
            .department(user.getDepartment())
            .employeeId(user.getEmployeeId())
            .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
            .branchId(user.getBranchId())
            .active(user.getActive())
            .accountLocked(user.getAccountLocked())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
