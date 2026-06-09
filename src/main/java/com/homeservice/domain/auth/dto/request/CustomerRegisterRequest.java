package com.homeservice.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class CustomerRegisterRequest {

	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 100, message = "Name must be 2–100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid 10-digit Indian mobile")
	private String mobile;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password min 8 characters")
	private String password;

	private String city;
}