package com.homeservice.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
	private Long userId;
	private String name;
	private String email;
	private String mobile;
	private String role;
	private String accessToken;
	@Builder.Default
	private String tokenType = "Bearer";
	private boolean mobileVerified;
}
