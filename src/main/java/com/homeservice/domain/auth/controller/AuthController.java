package com.homeservice.domain.auth.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.auth.dto.request.AdminRegisterRequest;
import com.homeservice.domain.auth.dto.request.CustomerRegisterRequest;
import com.homeservice.domain.auth.dto.request.LoginRequest;
import com.homeservice.domain.auth.dto.request.LoginWithOtpRequest;
import com.homeservice.domain.auth.dto.request.LogoutRequest;
import com.homeservice.domain.auth.dto.request.RefreshTokenRequest;
import com.homeservice.domain.auth.dto.request.SendOtpRequest;
import com.homeservice.domain.auth.dto.request.VerifyMobileRequest;
import com.homeservice.domain.auth.dto.request.WorkerRegisterRequest;
import com.homeservice.domain.auth.dto.response.AuthResponse;
import com.homeservice.domain.auth.dto.response.OtpSentResponse;
import com.homeservice.domain.auth.dto.response.TokenResponse;
import com.homeservice.domain.auth.service.AuthService;
import com.homeservice.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/customer/register")
    @Operation(summary = "Register customer")
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            registerCustomer(
                @Valid @RequestBody
                CustomerRegisterRequest req,
                HttpServletRequest http) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Registered. Verify your mobile.",
                authService.registerCustomer(req,
                    getDeviceInfo(http),
                    getClientIp(http))));
    }

    @PostMapping("/worker/register")
    @Operation(summary = "Register worker")
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            registerWorker(
                @Valid @RequestBody
                WorkerRegisterRequest req,
                HttpServletRequest http) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Registered. Verify your mobile.",
                authService.registerWorker(req,
                    getDeviceInfo(http),
                    getClientIp(http))));
    }

    @PostMapping("/admin/register")
    @Operation(summary = "Register admin")
    public ResponseEntity<ApiResponse<AuthResponse>>
            registerAdmin(
                @Valid @RequestBody
                AdminRegisterRequest req,
                HttpServletRequest http) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Admin registered.",
                authService.registerAdmin(req,
                    getDeviceInfo(http),
                    getClientIp(http))));
    }

    @PostMapping("/verify-mobile")
    @Operation(summary = "Verify mobile with OTP")
    public ResponseEntity<ApiResponse<Void>>
            verifyMobile(
                @Valid @RequestBody
                VerifyMobileRequest req) {

        String msg = authService.verifyMobile(req);
        return ResponseEntity.ok(
            ApiResponse.success(msg));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email + password")
    public ResponseEntity<ApiResponse<AuthResponse>>
            login(
                @Valid @RequestBody LoginRequest req,
                HttpServletRequest http) {

        return ResponseEntity.ok(
            ApiResponse.success("Login successful.",
                authService.loginWithPassword(req,
                    getDeviceInfo(http),
                    getClientIp(http))));
    }

    @PostMapping("/login/send-otp")
    @Operation(summary = "Send OTP for mobile login")
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            sendLoginOtp(
                @Valid @RequestBody
                SendOtpRequest req) {

        return ResponseEntity.ok(
            ApiResponse.success("OTP sent.",
                authService.sendLoginOtp(req)));
    }

    @PostMapping("/login/verify-otp")
    @Operation(summary = "Login with OTP")
    public ResponseEntity<ApiResponse<AuthResponse>>
            loginWithOtp(
                @Valid @RequestBody
                LoginWithOtpRequest req,
                HttpServletRequest http) {

        return ResponseEntity.ok(
            ApiResponse.success("Login successful.",
                authService.loginWithOtp(req,
                    getDeviceInfo(http),
                    getClientIp(http))));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend OTP")
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            resendOtp(
                @Valid @RequestBody
                SendOtpRequest req) {

        return ResponseEntity.ok(
            ApiResponse.success("OTP resent.",
                authService.resendOtp(req)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<TokenResponse>>
            refresh(
                @Valid @RequestBody
                RefreshTokenRequest req) {

        return ResponseEntity.ok(
            ApiResponse.success("Token refreshed.",
                authService.refreshToken(req)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current device")
    public ResponseEntity<ApiResponse<Void>>
            logout(
                @Valid @RequestBody
                LogoutRequest req) {

        authService.logout(req);
        return ResponseEntity.ok(
            ApiResponse.success("Logged out."));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout all devices")
    public ResponseEntity<ApiResponse<Void>>
            logoutAll(
                @AuthenticationPrincipal
                UserDetailsImpl current) {

        authService.logoutAllDevices(
                current.getUserId());
        return ResponseEntity.ok(
            ApiResponse.success(
                "Logged out from all devices."));
    }

    // ── Helpers ───────────────────────────────────────

    private String getDeviceInfo(
            HttpServletRequest r) {
        return r.getHeader("User-Agent");
    }

    private String getClientIp(
            HttpServletRequest r) {
        String ip = r.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank())
            ip = r.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank())
            ip = r.getRemoteAddr();
        if (ip != null && ip.contains(","))
            ip = ip.split(",")[0].trim();
        return ip;
    }
}