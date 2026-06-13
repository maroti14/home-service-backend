package com.homeservice.domain.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class RazorpayOrderResponse {

	private Long bookingId;
	private String razorpayOrderId;
	private BigDecimal amount;

	// currency always INR
	private String currency;

	// razorpay key ID
	// Flutter app needs this to open payment sheet
	private String keyId;
}
