package com.homeservice.infrastructure.location.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceDetailResponse {

	private String placeId;
	private String fullAddress;
	private String city;
	private String state;
	private String pincode;
	private Double latitude;
	private Double longitude;
}
