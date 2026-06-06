package com.homeservice.domain.servicetype.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ServicePricingResponse {

	private Long id;
	private Long cityId;
	private String cityName;
	private Long serviceTypeId;
	private String serviceTypeName;
	private BigDecimal basePrice;
	private BigDecimal pricePerExtraSlot;
	private Double platformFeePercentage;

	// Bike wash add-ons
	private BigDecimal engineCleaningPrice;
	private BigDecimal chainLubricationPrice;
	private BigDecimal tyrePollishPrice;

	// Cooking add-on
	private BigDecimal groceryAddonPrice;

	private Boolean isActive;
}
