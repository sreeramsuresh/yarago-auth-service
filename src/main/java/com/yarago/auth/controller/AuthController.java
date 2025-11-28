package com.yarago.auth.controller;

import com.yarago.auth.dto.*;
import com.yarago.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * Handles login, registration, token refresh, and logout
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * User login
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        request.setIpAddress(getClientIpAddress(httpRequest));
        request.setUserAgent(httpRequest.getHeader("User-Agent"));

        AuthResponse response = authService.login(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Login successful");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register new user")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "User registered successfully");
        result.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<Map<String, Object>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Token refreshed successfully");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    /**
     * Logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and revoke tokens")
    public ResponseEntity<Map<String, Object>> logout(Authentication authentication) {
        if (authentication != null) {
            authService.logout(authentication.getName());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logged out successfully");

        return ResponseEntity.ok(result);
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get authenticated user information")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        // User info already in JWT token, can be extracted from authentication
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", authentication.getPrincipal());

        return ResponseEntity.ok(result);
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if auth service is healthy")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "auth-service");

        return ResponseEntity.ok(result);
    }

    /**
     * Extract client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
