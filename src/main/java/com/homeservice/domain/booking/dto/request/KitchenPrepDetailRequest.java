package com.homeservice.domain.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KitchenPrepDetailRequest {

	private Boolean vegetableCutting = false;
	private Boolean masalaGrinding = false;
	private Boolean doughKneading = false;

	// quantity in kg
	private Double quantityKg;

	private String specialInstructions;
	private Boolean hasIngredients = true;
}
