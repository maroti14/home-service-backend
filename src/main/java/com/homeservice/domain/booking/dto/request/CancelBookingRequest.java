package com.homeservice.domain.booking.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBookingRequest {

	@Size(max = 300)
	private String reason;
}
