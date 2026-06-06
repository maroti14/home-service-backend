package com.homeservice.domain.city.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCityConfigRequest {

	@Min(value = 1, message = "Min radius is 1 km")
	@Max(value = 20, message = "Max radius is 20 km")
	private Double geoRadiusKm;

	private Double peakHourMultiplier;

	@Min(value = 1)
	@Max(value = 5)
	private Double minWorkerRating;

	@Min(value = 0)
	@Max(value = 50)
	private Double commissionPercentage;

	@Min(value = 1)
	@Max(value = 30)
	private Integer advanceBookingDays;

	@Min(value = 1)
	@Max(value = 5)
	private Integer maxDispatchRetries;

	@Min(value = 15)
	@Max(value = 60)
	private Integer jobAlertTimeoutSeconds;
}
