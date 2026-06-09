package com.homeservice.domain.auth.service;

import com.homeservice.common.enums.OtpPurpose;

public interface OtpService {
	void generateAndSend(String mobile, OtpPurpose purpose);

	void verifyOtp(String mobile, String code, OtpPurpose purpose);

	int getExpiryMinutes();
}