package com.homeservice.domain.auth.dto.request;

import com.homeservice.common.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequest {

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
	private String mobile;

	@NotNull(message = "OTP purpose is required")
	private OtpPurpose purpose;
}
