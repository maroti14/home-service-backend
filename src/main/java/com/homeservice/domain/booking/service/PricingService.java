package com.homeservice.domain.booking.service;

import com.homeservice.domain.booking.dto.request.CreateBookingRequest;
import com.homeservice.domain.booking.dto.response.PriceBreakdownResponse;

public interface PricingService {

	PriceBreakdownResponse calculate(CreateBookingRequest req);
}
