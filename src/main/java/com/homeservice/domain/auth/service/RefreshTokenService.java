package com.homeservice.domain.auth.service;

import com.homeservice.domain.auth.dto.response.TokenResponse;
import com.homeservice.domain.auth.entity.User;

public interface RefreshTokenService {
	String create(User user, String deviceInfo, String ipAddress);

	TokenResponse refresh(String refreshToken);

	void revoke(String refreshToken);

	void revokeAll(Long userId);
}