package com.homeservice.domain.booking.dto.request;

import com.homeservice.common.enums.CuisineType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CookingDetailRequest {

	@NotNull(message = "Cuisine type required")
	private CuisineType cuisineType;

	// list of dish names and quantities
	private List<DishItem> dishes;

	@Min(value = 1, message = "Minimum 1 serving")
	private Integer servings;

	// VEG / NON_VEG / VEGAN
	private String dietType;

	// comma separated allergens
	private String allergens;

	private Boolean groceryAddon = false;

	@Getter
	@Setter
	public static class DishItem {
		private String dishName;
		private Integer quantity;
	}
}
