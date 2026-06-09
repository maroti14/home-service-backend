package com.homeservice.domain.worker.controller;

import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.service.AdminWorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/workers")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Worker Management")
public class AdminWorkerController {

	private final AdminWorkerService adminService;

	@GetMapping
	@Operation(summary = "Get all workers")
	public ResponseEntity<ApiResponse<List<WorkerProfileResponse>>> getAllWorkers() {

		return ResponseEntity.ok(ApiResponse.success("Workers fetched.", adminService.getAllWorkers()));
	}

	@GetMapping("/{workerId}")
	@Operation(summary = "Get worker by ID")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>> getWorker(
			@PathVariable @Min(value = 1, message = "Invalid worker ID") Long workerId) {

		return ResponseEntity.ok(ApiResponse.success("Worker fetched.", adminService.getWorker(workerId)));
	}

	@PutMapping("/{workerId}/approve")
	@Operation(summary = "Approve worker account", description = "Worker can go online " + "only after approval")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>> approveWorker(
			@PathVariable @Min(value = 1) Long workerId) {

		return ResponseEntity
				.ok(ApiResponse.success("Worker approved successfully.", adminService.approveWorker(workerId)));
	}

	@PutMapping("/{workerId}/suspend")
	@Operation(summary = "Suspend worker account")
	public ResponseEntity<ApiResponse<WorkerProfileResponse>> suspendWorker(
			@PathVariable @Min(value = 1) Long workerId) {

		return ResponseEntity.ok(ApiResponse.success("Worker suspended.", adminService.suspendWorker(workerId)));
	}

	@PutMapping("/{workerId}/documents/{docId}/approve")
	@Operation(summary = "Approve a worker document")
	public ResponseEntity<ApiResponse<WorkerDocumentResponse>> approveDocument(@PathVariable @Min(1) Long workerId,
			@PathVariable @Min(1) Long docId) {

		return ResponseEntity.ok(ApiResponse.success("Document approved.",
				adminService.updateDocumentStatus(workerId, docId, DocumentStatus.APPROVED, null)));
	}

	@PutMapping("/{workerId}/documents/{docId}/reject")
	@Operation(summary = "Reject a worker document")
	public ResponseEntity<ApiResponse<WorkerDocumentResponse>> rejectDocument(@PathVariable @Min(1) Long workerId,
			@PathVariable @Min(1) Long docId,
			@RequestParam @NotBlank(message = "Rejection reason required") String reason) {

		return ResponseEntity.ok(ApiResponse.success("Document rejected.",
				adminService.updateDocumentStatus(workerId, docId, DocumentStatus.REJECTED, reason)));
	}

	@PutMapping("/{workerId}/service-tags/{tagId}/approve")
	@Operation(summary = "Approve a worker service tag")
	public ResponseEntity<ApiResponse<WorkerServiceTagResponse>> approveServiceTag(@PathVariable @Min(1) Long workerId,
			@PathVariable @Min(1) Long tagId) {

		return ResponseEntity
				.ok(ApiResponse.success("Service tag approved.", adminService.approveServiceTag(workerId, tagId)));
	}
}
