package com.homeservice.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class WorkerRegisterRequest {

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
	@Size(min = 8, message = "Password min 8 characters")
	private String password;

	private String city;
}