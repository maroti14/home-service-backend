package com.homeservice.domain.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DishwashingDetailRequest {

	// SMALL / MEDIUM / LARGE
	private String loadSize;

	private Boolean includeKitchenWipe = false;
}
