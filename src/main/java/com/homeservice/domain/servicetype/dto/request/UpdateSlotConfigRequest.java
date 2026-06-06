package com.homeservice.domain.servicetype.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSlotConfigRequest {

	@Min(value = 0, message = "Start hour min 0")
	@Max(value = 23, message = "Start hour max 23")
	private Integer startHour;

	@Min(value = 1, message = "End hour min 1")
	@Max(value = 24, message = "End hour max 24")
	private Integer endHour;

	private Integer slotIntervalMinutes;

	@Min(value = 1)
	@Max(value = 30)
	private Integer advanceBookingDays;

	private Boolean isActive;
}
