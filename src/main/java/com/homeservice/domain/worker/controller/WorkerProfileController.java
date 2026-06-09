package com.homeservice.domain.worker.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.worker.dto.request.UpdateWorkerProfileRequest;
import com.homeservice.domain.worker.dto.request.WorkerOnlineStatusRequest;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.service.WorkerProfileService;
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
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "Worker Profile")
public class WorkerProfileController {

	private final WorkerProfileService profileService;

	@GetMapping("/profile")
	@Operation(summary = "Get my profile")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>>

			getProfile(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Profile fetched.", profileService.getProfile(currentUser.getUserId())));
	}

	@PutMapping("/profile")
	@Operation(summary = "Update my profile")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>>

			updateProfile(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody UpdateWorkerProfileRequest req) {

		return ResponseEntity.ok(
				ApiResponse.success("Profile updated.", profileService.updateProfile(currentUser.getUserId(), req)));
	}

	@PutMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload profile photo")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>>

			uploadPhoto(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestParam("photo") MultipartFile photo) {

		return ResponseEntity.ok(ApiResponse.success("Photo uploaded.",
				profileService.uploadProfilePhoto(currentUser.getUserId(), photo)));
	}

	@PutMapping("/status")
	@Operation(summary = "Toggle online / offline", description = "Worker must be approved before " + "going online")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>>

			updateOnlineStatus(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody WorkerOnlineStatusRequest req) {

		return ResponseEntity.ok(ApiResponse.success(req.getIsOnline() ? "You are now online." : "You are now offline.",
				profileService.updateOnlineStatus(currentUser.getUserId(), req)));
	}
}
