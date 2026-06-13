package com.homeservice.domain.booking.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BathroomDetailRequest {

	@Min(value = 1, message = "Min 1 bathroom")
	private Integer bathroomCount;

	// STANDARD / DEEP
	private String cleanType;

	private Boolean includeExhaustFan = false;
	private Boolean includeMirrorPolish = false;
}
