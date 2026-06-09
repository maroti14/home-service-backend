package com.homeservice.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class VerifyMobileRequest {

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid 10-digit Indian mobile")
	private String mobile;

	@NotBlank(message = "OTP is required")
	@Size(min = 6, max = 6, message = "OTP must be 6 digits")
	private String otp;
}
