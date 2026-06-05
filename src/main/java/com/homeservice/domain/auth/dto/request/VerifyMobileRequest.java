package com.homeservice.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyMobileRequest {

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
	private String mobile;

	@NotBlank(message = "OTP is required")
	@Size(min = 6, max = 6, message = "OTP must be 6 digits")
	private String otp;
}
