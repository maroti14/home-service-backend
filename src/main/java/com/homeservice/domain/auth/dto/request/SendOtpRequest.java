package com.homeservice.domain.auth.dto.request;

import com.homeservice.common.enums.OtpPurpose;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class SendOtpRequest {

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid 10-digit Indian mobile")
	private String mobile;

	@NotNull(message = "Purpose is required")
	private OtpPurpose purpose;
}