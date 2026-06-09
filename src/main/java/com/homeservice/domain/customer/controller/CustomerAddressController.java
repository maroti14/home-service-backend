package com.homeservice.domain.customer.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.customer.dto.request.SaveAddressRequest;
import com.homeservice.domain.customer.dto.request.UpdateAddressRequest;
import com.homeservice.domain.customer.dto.response.CustomerAddressResponse;
import com.homeservice.domain.customer.service.CustomerAddressService;
import com.homeservice.infrastructure.location.dto.PlaceAutoCompleteResponse;
import com.homeservice.infrastructure.location.dto.PlaceDetailResponse;
import com.homeservice.infrastructure.location.dto.ReverseGeocodeResponse;
import com.homeservice.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer Address & Location")
public class CustomerAddressController {

	private final CustomerAddressService addressService;

	// ── Address CRUD ──────────────────────────────────

	@GetMapping("/addresses")
	@Operation(summary = "Get all my addresses")
	public ResponseEntity<ApiResponse<List<CustomerAddressResponse>>>

			getAddresses(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Addresses fetched.", addressService.getAddresses(currentUser.getUserId())));
	}

	@PostMapping("/addresses")
	@Operation(summary = "Save new address")
	public ResponseEntity<ApiResponse<CustomerAddressResponse>>

			saveAddress(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody SaveAddressRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Address saved.", addressService.saveAddress(currentUser.getUserId(), req)));
	}

	@PutMapping("/addresses/{addressId}")
	@Operation(summary = "Update an address")
	public ResponseEntity<ApiResponse<CustomerAddressResponse>>

			updateAddress(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@PathVariable @Min(value = 1, message = "Invalid address ID") Long addressId,
					@Valid @RequestBody UpdateAddressRequest req) {

		return ResponseEntity.ok(ApiResponse.success("Address updated.",
				addressService.updateAddress(currentUser.getUserId(), addressId, req)));
	}

	@DeleteMapping("/addresses/{addressId}")
	@Operation(summary = "Delete an address")
	public ResponseEntity<ApiResponse<Void>> deleteAddress(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable @Min(value = 1, message = "Invalid address ID") Long addressId) {

		addressService.deleteAddress(currentUser.getUserId(), addressId);

		return ResponseEntity.ok(ApiResponse.success("Address deleted."));
	}

	@PutMapping("/addresses/{addressId}/set-default")
	@Operation(summary = "Set address as default")
	public ResponseEntity<ApiResponse<CustomerAddressResponse>>

			setDefault(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@PathVariable @Min(value = 1, message = "Invalid address ID") Long addressId) {

		return ResponseEntity.ok(ApiResponse.success("Default address updated.",
				addressService.setDefaultAddress(currentUser.getUserId(), addressId)));
	}

	// ── Location APIs ─────────────────────────────────

	@GetMapping("/location/reverse-geocode")
	@Operation(summary = "Convert GPS to address", description = "Call this when Flutter "
			+ "app gets device GPS location")
	public ResponseEntity<ApiResponse<ReverseGeocodeResponse>>

			reverseGeocode(
					@RequestParam @DecimalMin(value = "-90.0", message = "Invalid latitude") @DecimalMax(value = "90.0", message = "Invalid latitude") Double latitude,
					@RequestParam @DecimalMin(value = "-180.0", message = "Invalid longitude") @DecimalMax(value = "180.0", message = "Invalid longitude") Double longitude) {

		return ResponseEntity
				.ok(ApiResponse.success("Address fetched.", addressService.reverseGeocode(latitude, longitude)));
	}

	@GetMapping("/location/autocomplete")
	@Operation(summary = "Search address suggestions", description = "Google Places autocomplete "
			+ "for manual address entry")
	public ResponseEntity<ApiResponse<PlaceAutoCompleteResponse>>

			autocomplete(@RequestParam @NotBlank(message = "Search query required") String query,
					@RequestParam(required = false) String sessionToken) {

		return ResponseEntity
				.ok(ApiResponse.success("Suggestions fetched.", addressService.autocomplete(query, sessionToken)));
	}

	@GetMapping("/location/place-details")
	@Operation(summary = "Get full address from place ID", description = "Call after user selects "
			+ "a suggestion from autocomplete")
	public ResponseEntity<ApiResponse<PlaceDetailResponse>>

			getPlaceDetails(@RequestParam @NotBlank(message = "Place ID required") String placeId) {

		return ResponseEntity
				.ok(ApiResponse.success("Place details fetched.", addressService.getPlaceDetails(placeId)));
	}
}
