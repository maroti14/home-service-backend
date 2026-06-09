package com.homeservice.domain.worker.controller;

import com.homeservice.common.enums.DocumentType;
import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.worker.dto.request.SaveAvailabilityRequest;
import com.homeservice.domain.worker.dto.request.SaveCuisineTagRequest;
import com.homeservice.domain.worker.dto.request.SaveServiceTagRequest;
import com.homeservice.domain.worker.dto.response.WorkerAvailabilityResponse;
import com.homeservice.domain.worker.dto.response.WorkerCuisineTagResponse;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.service.WorkerDocumentService;
import com.homeservice.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "Worker Documents, Tags & Availability")
public class WorkerDocumentController {

	private final WorkerDocumentService docService;

	// ── Documents ─────────────────────────────────────

	@PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Upload a document", description = "Supported types: AADHAAR_FRONT, "
			+ "AADHAAR_BACK, PAN_CARD, " + "FSSAI_CERTIFICATE")
	public ResponseEntity<ApiResponse<WorkerDocumentResponse>>

			uploadDocument(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestParam DocumentType documentType, @RequestParam("file") MultipartFile file) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Document uploaded. " + "Pending admin approval.",
						docService.uploadDocument(currentUser.getUserId(), documentType, file)));
	}

	@GetMapping("/documents")
	@Operation(summary = "Get all my documents")
	public ResponseEntity<ApiResponse<List<WorkerDocumentResponse>>>

			getDocuments(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Documents fetched.", docService.getDocuments(currentUser.getUserId())));
	}

	// ── Service Tags ──────────────────────────────────

	@PostMapping("/service-tags")
	@Operation(summary = "Add a service tag", description = "Add a service you can perform. "
			+ "Admin will approve it.")
	public ResponseEntity<ApiResponse<WorkerServiceTagResponse>>

			addServiceTag(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody SaveServiceTagRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Service tag added. " + "Pending admin approval.",
						docService.addServiceTag(currentUser.getUserId(), req)));
	}

	@DeleteMapping("/service-tags/{tagId}")
	@Operation(summary = "Remove a service tag")
	public ResponseEntity<ApiResponse<Void>> removeServiceTag(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable @Min(value = 1, message = "Invalid tag ID") Long tagId) {

		docService.removeServiceTag(currentUser.getUserId(), tagId);

		return ResponseEntity.ok(ApiResponse.success("Service tag removed."));
	}

	@GetMapping("/service-tags")
	@Operation(summary = "Get my service tags")
	public ResponseEntity<ApiResponse<List<WorkerServiceTagResponse>>>

			getServiceTags(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Service tags fetched.", docService.getServiceTags(currentUser.getUserId())));
	}

	// ── Cuisine Tags ──────────────────────────────────

	@PostMapping("/cuisine-tags")
	@Operation(summary = "Add a cuisine tag", description = "Only for workers with Cooking "
			+ "or Kitchen Prep service tag")
	public ResponseEntity<ApiResponse<WorkerCuisineTagResponse>>

			addCuisineTag(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody SaveCuisineTagRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.success("Cuisine tag added.", docService.addCuisineTag(currentUser.getUserId(), req)));
	}

	@DeleteMapping("/cuisine-tags/{tagId}")
	@Operation(summary = "Remove a cuisine tag")
	public ResponseEntity<ApiResponse<Void>> removeCuisineTag(@AuthenticationPrincipal UserDetailsImpl currentUser,
			@PathVariable @Min(value = 1, message = "Invalid tag ID") Long tagId) {

		docService.removeCuisineTag(currentUser.getUserId(), tagId);

		return ResponseEntity.ok(ApiResponse.success("Cuisine tag removed."));
	}

	@GetMapping("/cuisine-tags")
	@Operation(summary = "Get my cuisine tags")
	public ResponseEntity<ApiResponse<List<WorkerCuisineTagResponse>>>

			getCuisineTags(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Cuisine tags fetched.", docService.getCuisineTags(currentUser.getUserId())));
	}

	// ── Availability ──────────────────────────────────

	@PostMapping("/availability")
	@Operation(summary = "Save availability schedule", description = "Replaces all existing availability. "
			+ "Send all days at once.")
	public ResponseEntity<ApiResponse<List<WorkerAvailabilityResponse>>>

			saveAvailability(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody SaveAvailabilityRequest req) {

		return ResponseEntity.ok(
				ApiResponse.success("Availability saved.", docService.saveAvailability(currentUser.getUserId(), req)));
	}

	@GetMapping("/availability")
	@Operation(summary = "Get my availability")
	public ResponseEntity<ApiResponse<List<WorkerAvailabilityResponse>>>

			getAvailability(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity
				.ok(ApiResponse.success("Availability fetched.", docService.getAvailability(currentUser.getUserId())));
	}
}
