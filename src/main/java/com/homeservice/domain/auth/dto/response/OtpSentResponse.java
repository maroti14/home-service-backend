package com.homeservice.domain.auth.dto.response;

import lombok.*;

@Getter
@Builder
public class OtpSentResponse {
	private String mobile;
	private String message;
	private int expiresInMinutes;
}
