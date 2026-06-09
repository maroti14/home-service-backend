package com.homeservice.domain.auth.service;

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

public interface AuthService {

    OtpSentResponse registerCustomer(
            CustomerRegisterRequest req,
            String deviceInfo, String ip);

    OtpSentResponse registerWorker(
            WorkerRegisterRequest req,
            String deviceInfo, String ip);

    AuthResponse registerAdmin(
            AdminRegisterRequest req,
            String deviceInfo, String ip);

    String verifyMobile(VerifyMobileRequest req);

    AuthResponse loginWithPassword(
            LoginRequest req,
            String deviceInfo, String ip);

    OtpSentResponse sendLoginOtp(
            SendOtpRequest req);

    AuthResponse loginWithOtp(
            LoginWithOtpRequest req,
            String deviceInfo, String ip);

    OtpSentResponse resendOtp(SendOtpRequest req);

    TokenResponse refreshToken(
            RefreshTokenRequest req);

    void logout(LogoutRequest req);

    void logoutAllDevices(Long userId);
}