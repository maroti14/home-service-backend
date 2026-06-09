package com.homeservice.domain.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerProfileRequest {

	@Size(min = 2, max = 100, message = "Name must be 2–100 characters")
	private String name;

	@Email(message = "Invalid email format")
	private String email;

	@Size(max = 100, message = "City max 100 characters")
	private String city;
}
