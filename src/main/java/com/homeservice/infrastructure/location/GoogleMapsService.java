package com.homeservice.infrastructure.location;

import com.homeservice.infrastructure.location.dto.PlaceAutoCompleteResponse;
import com.homeservice.infrastructure.location.dto.PlaceDetailResponse;
import com.homeservice.infrastructure.location.dto.ReverseGeocodeResponse;

public interface GoogleMapsService {

	// convert lat/lng to address
	ReverseGeocodeResponse reverseGeocode(Double latitude, Double longitude);

	// search address suggestions
	PlaceAutoCompleteResponse autocomplete(String query, String sessionToken);

	// get full details of a place
	PlaceDetailResponse getPlaceDetails(String placeId);
}
