package com.homeservice.domain.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PriceBreakdownResponse {

	private BigDecimal basePrice;
	private BigDecimal extraSlotsPrice;
	private BigDecimal addOnsPrice;
	private BigDecimal platformFee;
	private BigDecimal totalAmount;
	private BigDecimal workerEarnings;

	// platform fee percentage
	private Double platformFeePercentage;

	// breakdown description
	private String description;
}