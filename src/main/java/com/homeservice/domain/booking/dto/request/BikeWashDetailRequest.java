package com.homeservice.domain.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BikeWashDetailRequest {

	// SCOOTER / STANDARD / SPORTS / CRUISER
	@NotBlank(message = "Bike type required")
	private String bikeType;

	// QUICK_WASH / FULL_DETAIL
	@NotBlank(message = "Wash type required")
	private String washType;

	private Boolean engineCleaning = false;
	private Boolean chainLubrication = false;
	private Boolean tyrePollish = false;

	// where the bike is parked
	private String parkingLocation;
	private String floorOrSpot;
}
