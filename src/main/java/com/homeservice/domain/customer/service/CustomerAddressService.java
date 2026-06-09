package com.homeservice.domain.customer.service;

import com.homeservice.domain.customer.dto.request.SaveAddressRequest;
import com.homeservice.domain.customer.dto.request.UpdateAddressRequest;
import com.homeservice.domain.customer.dto.response.CustomerAddressResponse;
import com.homeservice.infrastructure.location.dto.PlaceAutoCompleteResponse;
import com.homeservice.infrastructure.location.dto.PlaceDetailResponse;
import com.homeservice.infrastructure.location.dto.ReverseGeocodeResponse;

import java.util.List;

public interface CustomerAddressService {

	List<CustomerAddressResponse> getAddresses(Long customerId);

	CustomerAddressResponse saveAddress(Long customerId, SaveAddressRequest req);

	CustomerAddressResponse updateAddress(Long customerId, Long addressId, UpdateAddressRequest req);

	void deleteAddress(Long customerId, Long addressId);

	CustomerAddressResponse setDefaultAddress(Long customerId, Long addressId);

	// location helpers
	ReverseGeocodeResponse reverseGeocode(Double latitude, Double longitude);

	PlaceAutoCompleteResponse autocomplete(String query, String sessionToken);

	PlaceDetailResponse getPlaceDetails(String placeId);
}