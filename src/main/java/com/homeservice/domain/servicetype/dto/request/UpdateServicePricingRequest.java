package com.homeservice.domain.servicetype.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateServicePricingRequest {

	@DecimalMin(value = "0.0", message = "Base price cannot be negative")
	private BigDecimal basePrice;

	@DecimalMin(value = "0.0")
	private BigDecimal pricePerExtraSlot;

	private Double platformFeePercentage;

	// Bike Wash add-ons
	private BigDecimal engineCleaningPrice;
	private BigDecimal chainLubricationPrice;
	private BigDecimal tyrePollishPrice;

	// Cooking add-on
	private BigDecimal groceryAddonPrice;

	private Boolean isActive;
}
