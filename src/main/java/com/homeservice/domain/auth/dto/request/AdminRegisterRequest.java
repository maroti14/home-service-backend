package com.homeservice.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class AdminRegisterRequest {

	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 100)
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid 10-digit Indian mobile")
	private String mobile;

	@NotBlank(message = "Password is required")
	@Size(min = 8)
	private String password;

	@NotBlank(message = "Admin secret required")
	private String adminSecret;
}