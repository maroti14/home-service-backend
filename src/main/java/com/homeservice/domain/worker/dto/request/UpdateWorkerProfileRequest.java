package com.homeservice.domain.worker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkerProfileRequest {

	@Size(min = 2, max = 100, message = "Name must be 2–100 characters")
	private String name;

	@Email(message = "Invalid email format")
	private String email;

	@Size(max = 100)
	private String city;

	@Size(max = 500, message = "Bio max 500 characters")
	private String bio;

	@Min(value = 0, message = "Experience cannot be negative")
	@Max(value = 50, message = "Experience max 50 years")
	private Integer experienceYears;
}
