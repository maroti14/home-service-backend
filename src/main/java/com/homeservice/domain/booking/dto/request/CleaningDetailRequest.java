package com.homeservice.domain.booking.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CleaningDetailRequest {

	// STANDARD / DEEP / MOVE_IN_OUT
	private String cleanType;

	@Min(value = 1)
	private Integer roomCount;

	private Boolean includeKitchen = false;
	private Boolean includeBathroom = false;
}
