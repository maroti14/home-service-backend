package com.homeservice.domain.auth.controller;



import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.auth.dto.request.*;
import com.homeservice.domain.auth.dto.response.AuthResponse;
import com.homeservice.domain.auth.dto.response.OtpSentResponse;
import com.homeservice.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication",
     description = "Register, verify mobile, and login")
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/admin/register")
    @Operation(summary = "Register admin (secret required)")
    public ResponseEntity<ApiResponse<AuthResponse>>
            registerAdmin(
                @Valid @RequestBody
                AdminRegisterRequest req) {

        AuthResponse result = authService.registerAdmin(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Admin registered.", result));
    }

    // ─────────────────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────────────────

    @PostMapping("/customer/register")
    @Operation(
        summary = "Register a new customer",
        description = "Creates account and sends " +
                      "OTP to mobile for verification"
    )
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            registerCustomer(
                @Valid @RequestBody
                CustomerRegisterRequest req) {

        OtpSentResponse result =
                authService.registerCustomer(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Registration successful. " +
                        "Verify your mobile.", result));
    }

    @PostMapping("/worker/register")
    @Operation(
        summary = "Register a new worker",
        description = "Creates worker account and sends " +
                      "OTP to mobile for verification"
    )
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            registerWorker(
                @Valid @RequestBody
                WorkerRegisterRequest req) {

        OtpSentResponse result =
                authService.registerWorker(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Registration successful. " +
                        "Verify your mobile.", result));
    }

    // ─────────────────────────────────────────────────────
    // VERIFY MOBILE
    // ─────────────────────────────────────────────────────

    @PostMapping("/verify-mobile")
    @Operation(
        summary = "Verify mobile with OTP",
        description = "Must be called after registration " +
                      "before first login"
    )
    public ResponseEntity<ApiResponse<Void>>
            verifyMobile(
                @Valid @RequestBody
                VerifyMobileRequest req) {

        String message = authService.verifyMobile(req);

        return ResponseEntity.ok(
                ApiResponse.success(message));
    }

    // ─────────────────────────────────────────────────────
    // LOGIN — password
    // ─────────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(
        summary = "Login with email + password",
        description = "Returns JWT access token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>>
            loginWithPassword(
                @Valid @RequestBody
                LoginRequest req) {

        AuthResponse result = authService.loginWithPassword(
                req.getEmail(), req.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success("Login successful.",
                                    result));
    }

    // ─────────────────────────────────────────────────────
    // LOGIN — OTP flow (2 steps)
    // ─────────────────────────────────────────────────────

    @PostMapping("/login/send-otp")
    @Operation(
        summary = "Send OTP for mobile login",
        description = "Step 1 of OTP login — " +
                      "sends OTP to registered mobile"
    )
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            sendLoginOtp(
                @Valid @RequestBody
                SendOtpRequest req) {

        OtpSentResponse result =
                authService.sendLoginOtp(req);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "OTP sent.", result));
    }

    @PostMapping("/login/verify-otp")
    @Operation(
        summary = "Login with OTP",
        description = "Step 2 of OTP login — " +
                      "verify OTP and receive JWT token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>>
            loginWithOtp(
                @Valid @RequestBody
                LoginWithOtpRequest req) {

        AuthResponse result =
                authService.loginWithOtp(req);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful.", result));
    }

    // ─────────────────────────────────────────────────────
    // RESEND OTP
    // ─────────────────────────────────────────────────────

    @PostMapping("/resend-otp")
    @Operation(
        summary = "Resend OTP",
        description = "Works for both " +
                      "MOBILE_VERIFICATION and LOGIN purposes"
    )
    public ResponseEntity<ApiResponse<OtpSentResponse>>
            resendOtp(
                @Valid @RequestBody
                SendOtpRequest req) {

        OtpSentResponse result =
                authService.resendOtp(req);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "OTP resent.", result));
    }
}