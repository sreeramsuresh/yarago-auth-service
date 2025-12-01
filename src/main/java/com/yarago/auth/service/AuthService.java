package com.yarago.auth.service;

import com.yarago.auth.dto.*;
import com.yarago.auth.entity.RefreshToken;
import com.yarago.auth.entity.Role;
import com.yarago.auth.entity.User;
import com.yarago.auth.repository.RefreshTokenRepository;
import com.yarago.auth.repository.RoleRepository;
import com.yarago.auth.repository.UserRepository;
import com.yarago.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication Service
 * Handles login, registration, token refresh, and logout operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int MAX_ACTIVE_SESSIONS = 5;

    /**
     * Authenticate user and generate tokens
     * Supports login with both username and email
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Check if account is locked
        if (user.getAccountLocked()) {
            throw new BadCredentialsException("Account is locked due to multiple failed login attempts");
        }

        try {
            // Authenticate using the identifier (username or email)
            // CustomUserDetailsService will handle finding the user by username or email
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Reset failed attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Load user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), user.getBranchId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // Save refresh token
            saveRefreshToken(user, refreshToken, request.getIpAddress(), request.getUserAgent());

            log.info("User {} logged in successfully", user.getUsername());

            return buildAuthResponse(accessToken, refreshToken, user);

        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * Register new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail()); // Will be encrypted by service layer
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName()); // Will be encrypted
        user.setLastName(request.getLastName()); // Will be encrypted
        user.setPhoneNumber(request.getPhoneNumber()); // Will be encrypted
        user.setDesignation(request.getDesignation());
        user.setDepartment(request.getDepartment());
        user.setEmployeeId(request.getEmployeeId());
        user.setBranchId(request.getBranchId());
        user.setActive(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setPasswordChangedAt(LocalDateTime.now());

        // Assign roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
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

        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), user.getBranchId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Save refresh token
        saveRefreshToken(user, refreshToken, null, null);

        log.info("User {} registered successfully", user.getUsername());

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshTokenValue)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));

        // Check if token is valid
        if (!refreshToken.isValid()) {
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();

        // Generate new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails, user.getId(), user.getBranchId());

        log.info("Access token refreshed for user {}", user.getUsername());

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshTokenValue) // Return same refresh token
            .tokenType("Bearer")
            .expiresIn(jwtUtil.getAccessTokenExpirationInSeconds())
            .user(buildUserInfo(user))
            .build();
    }

    /**
     * Logout user and revoke tokens
     */
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        log.info("User {} logged out", username);
    }

    /**
     * Save refresh token to database
     */
    private void saveRefreshToken(User user, String token, String ipAddress, String userAgent) {
        // Check active sessions limit
        long activeSessionsCount = refreshTokenRepository.countValidTokensByUser(user, LocalDateTime.now());
        if (activeSessionsCount >= MAX_ACTIVE_SESSIONS) {
            // Revoke oldest tokens
            refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshTokenExpirationInSeconds()));
        refreshToken.setRevoked(false);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Handle failed login attempts
     */
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setAccountLocked(true);
            log.warn("Account locked for user {} due to {} failed login attempts", user.getUsername(), attempts);
        }

        userRepository.save(user);
    }

    /**
     * Build authentication response
     */
    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtUtil.getAccessTokenExpirationInSeconds())
            .user(buildUserInfo(user))
            .build();
    }

    /**
     * Build user info DTO
     */
    private AuthResponse.UserInfo buildUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
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
            .build();
    }
}
