package com.homeservice.domain.city.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CityConfigResponse {

	private Long id;
	private Long cityId;
	private String cityName;
	private Double geoRadiusKm;
	private Double peakHourMultiplier;
	private Double minWorkerRating;
	private Double commissionPercentage;
	private Integer advanceBookingDays;
	private Integer maxDispatchRetries;
	private Integer jobAlertTimeoutSeconds;
}
