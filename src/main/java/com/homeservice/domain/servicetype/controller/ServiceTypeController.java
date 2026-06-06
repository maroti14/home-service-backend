package com.homeservice.domain.servicetype.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.servicetype.dto.request.CreateServiceTypeRequest;
import com.homeservice.domain.servicetype.dto.request.UpdateServiceTypeRequest;
import com.homeservice.domain.servicetype.dto.response.ServicePricingResponse;
import com.homeservice.domain.servicetype.dto.response.ServiceTypeResponse;
import com.homeservice.domain.servicetype.service.ServicePricingService;
import com.homeservice.domain.servicetype.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Service Type Management")
public class ServiceTypeController {

	private final ServiceTypeService serviceTypeService;
	private final ServicePricingService pricingService;

	// ── PUBLIC ────────────────────────────────────────

	@GetMapping("/api/v1/services")
	@Operation(summary = "Get all active services")
	public ResponseEntity<ApiResponse<List<ServiceTypeResponse>>> getAllActive() {

		return ResponseEntity.ok(ApiResponse.success("Services fetched.", serviceTypeService.getAllActive()));
	}

	@GetMapping("/api/v1/services/{serviceId}")
	@Operation(summary = "Get service by id")
	public ResponseEntity<ApiResponse<ServiceTypeResponse>> getById(@PathVariable Long serviceId) {

		return ResponseEntity.ok(ApiResponse.success("Service fetched.", serviceTypeService.getById(serviceId)));
	}

	@GetMapping("/api/v1/pricing/{cityId}/{serviceId}")
	@Operation(summary = "Get pricing for city + service")
	public ResponseEntity<ApiResponse<ServicePricingResponse>> getPricing(@PathVariable Long cityId,
			@PathVariable Long serviceId) {

		return ResponseEntity
				.ok(ApiResponse.success("Pricing fetched.", pricingService.getByCityAndService(cityId, serviceId)));
	}

	@GetMapping("/api/v1/pricing/{cityId}")
	@Operation(summary = "Get all pricing for a city")
	public ResponseEntity<ApiResponse<List<ServicePricingResponse>>> getAllPricingForCity(@PathVariable Long cityId) {

		return ResponseEntity.ok(ApiResponse.success("City pricing fetched.", pricingService.getByCity(cityId)));
	}

	// ── ADMIN ONLY ────────────────────────────────────

	@GetMapping("/api/v1/admin/services")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — get all services")
	public ResponseEntity<ApiResponse<List<ServiceTypeResponse>>> getAll() {

		return ResponseEntity.ok(ApiResponse.success("All services fetched.", serviceTypeService.getAll()));
	}

	@PostMapping("/api/v1/admin/services")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — create service type")
	public ResponseEntity<ApiResponse<ServiceTypeResponse>> create(@Valid @RequestBody CreateServiceTypeRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Service type created.", serviceTypeService.create(req)));
	}

	@PutMapping("/api/v1/admin/services/{serviceId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — update service type")
	public ResponseEntity<ApiResponse<ServiceTypeResponse>> update(@PathVariable Long serviceId,
			@Valid @RequestBody UpdateServiceTypeRequest req) {

		return ResponseEntity
				.ok(ApiResponse.success("Service type updated.", serviceTypeService.update(serviceId, req)));
	}
}
