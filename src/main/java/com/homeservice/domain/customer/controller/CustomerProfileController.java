package com.homeservice.domain.customer.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.customer.dto.request.UpdateCustomerProfileRequest;
import com.homeservice.domain.customer.dto.response.CustomerProfileResponse;
import com.homeservice.domain.customer.service.CustomerProfileService;
import com.homeservice.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer Profile")
public class CustomerProfileController {

	private final CustomerProfileService profileService;

	@GetMapping("/profile")
	@Operation(summary = "Get my profile")
	public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(
			@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Profile fetched.", profileService.getProfile(currentUser.getUserId())));
	}

	@PutMapping("/profile")
	@Operation(summary = "Update my profile")
	public ResponseEntity<ApiResponse<CustomerProfileResponse>>

			updateProfile(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody UpdateCustomerProfileRequest req) {

		return ResponseEntity.ok(
				ApiResponse.success("Profile updated.", profileService.updateProfile(currentUser.getUserId(), req)));
	}

	@PutMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload profile photo")
	public ResponseEntity<ApiResponse<CustomerProfileResponse>>

			uploadPhoto(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestParam("photo") MultipartFile photo) {

		return ResponseEntity.ok(ApiResponse.success("Photo uploaded.",
				profileService.uploadProfilePhoto(currentUser.getUserId(), photo)));
	}
}
