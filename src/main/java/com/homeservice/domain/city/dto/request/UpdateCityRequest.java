package com.homeservice.domain.city.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCityRequest {

	@Size(min = 2, max = 100)
	private String name;

	private String state;

	private Boolean isActive;
}
