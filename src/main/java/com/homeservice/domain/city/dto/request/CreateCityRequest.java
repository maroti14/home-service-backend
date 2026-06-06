package com.homeservice.domain.city.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCityRequest {

	@NotBlank(message = "City name is required")
	@Size(min = 2, max = 100)
	private String name;

	@NotBlank(message = "State is required")
	private String state;

	// uppercase code e.g. PUNE, MUMBAI
	@NotBlank(message = "City code is required")
	@Pattern(regexp = "^[A-Z]{2,20}$", message = "City code must be uppercase " + "letters only e.g. PUNE")
	private String cityCode;
}
