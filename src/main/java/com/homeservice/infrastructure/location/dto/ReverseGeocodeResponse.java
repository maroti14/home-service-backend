package com.homeservice.infrastructure.location.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReverseGeocodeResponse {

	private String fullAddress;
	private String houseNumber;
	private String street;
	private String landmark;
	private String city;
	private String state;
	private String pincode;
	private Double latitude;
	private Double longitude;
}
