package com.homeservice.domain.customer.service;

import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.common.util.SanitizationUtils;
import com.homeservice.domain.customer.dto.request.SaveAddressRequest;
import com.homeservice.domain.customer.dto.request.UpdateAddressRequest;
import com.homeservice.domain.customer.dto.response.CustomerAddressResponse;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.entity.CustomerAddress;
import com.homeservice.domain.customer.mapper.CustomerMapper;
import com.homeservice.domain.customer.repository.CustomerAddressRepository;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.infrastructure.location.GoogleMapsService;
import com.homeservice.infrastructure.location.dto.PlaceAutoCompleteResponse;
import com.homeservice.infrastructure.location.dto.PlaceDetailResponse;
import com.homeservice.infrastructure.location.dto.ReverseGeocodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerAddressServiceImpl implements CustomerAddressService {

	private final CustomerAddressRepository addressRepo;
	private final CustomerRepository customerRepo;
	private final GoogleMapsService mapsService;
	private final CustomerMapper customerMapper;

	// max addresses per customer
	private static final int MAX_ADDRESSES = 5;

	@Override
	@Transactional(readOnly = true)
	public List<CustomerAddressResponse> getAddresses(Long customerId) {

		List<CustomerAddress> addresses = addressRepo.findByCustomerIdAndIsDeletedFalse(customerId);

		return customerMapper.toAddressResponseList(addresses);
	}

	@Override
	@Transactional
	public CustomerAddressResponse saveAddress(Long customerId, SaveAddressRequest req) {

		Customer customer = findCustomer(customerId);

		// check address limit
		long count = addressRepo.countByCustomerIdAndIsDeletedFalse(customerId);

		if (count >= MAX_ADDRESSES) {
			throw new InvalidInputException(
					"Maximum " + MAX_ADDRESSES + " addresses allowed. " + "Please delete one first.");
		}

		// if this is first address
		// or explicitly set as default
		boolean shouldBeDefault = req.getIsDefault() || count == 0;

		// clear existing defaults
		// if this should be default
		if (shouldBeDefault) {
			addressRepo.clearAllDefaults(customerId);
		}

		CustomerAddress address = CustomerAddress.builder().customer(customer)
				.fullAddress(SanitizationUtils.sanitizeText(req.getFullAddress()))
				.houseNumber(SanitizationUtils.sanitize(req.getHouseNumber()))
				.buildingName(SanitizationUtils.sanitize(req.getBuildingName()))
				.street(SanitizationUtils.sanitize(req.getStreet()))
				.landmark(SanitizationUtils.sanitize(req.getLandmark())).city(SanitizationUtils.sanitize(req.getCity()))
				.state(SanitizationUtils.sanitize(req.getState())).pincode(req.getPincode()).latitude(req.getLatitude())
				.longitude(req.getLongitude()).label(req.getLabel()).isDefault(shouldBeDefault).isDeleted(false)
				.build();

		addressRepo.save(address);

		log.info("Address saved | " + "customerId={} label={}", customerId, req.getLabel());

		return customerMapper.toAddressResponse(address);
	}

	@Override
	@Transactional
	public CustomerAddressResponse updateAddress(Long customerId, Long addressId, UpdateAddressRequest req) {

		CustomerAddress address = findAddress(addressId, customerId);

		if (req.getFullAddress() != null)
			address.setFullAddress(SanitizationUtils.sanitizeText(req.getFullAddress()));
		if (req.getHouseNumber() != null)
			address.setHouseNumber(SanitizationUtils.sanitize(req.getHouseNumber()));
		if (req.getBuildingName() != null)
			address.setBuildingName(SanitizationUtils.sanitize(req.getBuildingName()));
		if (req.getStreet() != null)
			address.setStreet(SanitizationUtils.sanitize(req.getStreet()));
		if (req.getLandmark() != null)
			address.setLandmark(SanitizationUtils.sanitize(req.getLandmark()));
		if (req.getCity() != null)
			address.setCity(SanitizationUtils.sanitize(req.getCity()));
		if (req.getState() != null)
			address.setState(SanitizationUtils.sanitize(req.getState()));
		if (req.getPincode() != null)
			address.setPincode(req.getPincode());
		if (req.getLatitude() != null)
			address.setLatitude(req.getLatitude());
		if (req.getLongitude() != null)
			address.setLongitude(req.getLongitude());
		if (req.getLabel() != null)
			address.setLabel(req.getLabel());

		// handle default change
		if (Boolean.TRUE.equals(req.getIsDefault()) && !address.getIsDefault()) {
			addressRepo.clearAllDefaults(customerId);
			address.setIsDefault(true);
		}

		addressRepo.save(address);

		log.info("Address updated | " + "addressId={}", addressId);

		return customerMapper.toAddressResponse(address);
	}

	@Override
	@Transactional
	public void deleteAddress(Long customerId, Long addressId) {

		CustomerAddress address = findAddress(addressId, customerId);

		// soft delete
		address.setIsDeleted(true);
		address.setIsDefault(false);
		addressRepo.save(address);

		// if this was the default address
		// auto-set next available as default
		List<CustomerAddress> remaining = addressRepo.findByCustomerIdAndIsDeletedFalse(customerId);

		if (!remaining.isEmpty()) {
			remaining.get(0).setIsDefault(true);
			addressRepo.save(remaining.get(0));
		}

		log.info("Address deleted | " + "addressId={}", addressId);
	}

	@Override
	@Transactional
	public CustomerAddressResponse setDefaultAddress(Long customerId, Long addressId) {

		CustomerAddress address = findAddress(addressId, customerId);

		// clear all defaults first
		addressRepo.clearAllDefaults(customerId);

		// set this as default
		address.setIsDefault(true);
		addressRepo.save(address);

		log.info("Default address set | " + "addressId={}", addressId);

		return customerMapper.toAddressResponse(address);
	}

	@Override
	public ReverseGeocodeResponse reverseGeocode(Double latitude, Double longitude) {
		return mapsService.reverseGeocode(latitude, longitude);
	}

	@Override
	public PlaceAutoCompleteResponse autocomplete(String query, String sessionToken) {
		return mapsService.autocomplete(query, sessionToken);
	}

	@Override
	public PlaceDetailResponse getPlaceDetails(String placeId) {
		return mapsService.getPlaceDetails(placeId);
	}

	// ── Private helpers ───────────────────────────────

	private Customer findCustomer(Long customerId) {
		return customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
	}

	private CustomerAddress findAddress(Long addressId, Long customerId) {
		return addressRepo.findByIdAndCustomerIdAndIsDeletedFalse(addressId, customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
	}
}
