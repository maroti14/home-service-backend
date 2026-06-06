package com.homeservice.domain.city.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.city.dto.request.CreateCityRequest;
import com.homeservice.domain.city.dto.request.UpdateCityConfigRequest;
import com.homeservice.domain.city.dto.request.UpdateCityRequest;
import com.homeservice.domain.city.dto.response.CityConfigResponse;
import com.homeservice.domain.city.dto.response.CityResponse;
import com.homeservice.domain.city.service.CityService;
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
@Tag(name = "City Management")
public class CityController {

	private final CityService cityService;

	// ── PUBLIC ────────────────────────────────────────

	@GetMapping("/api/v1/cities")
	@Operation(summary = "Get all active cities")
	public ResponseEntity<ApiResponse<List<CityResponse>>> getAllActiveCities() {

		return ResponseEntity.ok(ApiResponse.success("Active cities fetched.", cityService.getAllActiveCities()));
	}

	@GetMapping("/api/v1/cities/{cityId}")
	@Operation(summary = "Get city by id")
	public ResponseEntity<ApiResponse<CityResponse>> getCityById(@PathVariable Long cityId) {

		return ResponseEntity.ok(ApiResponse.success("City fetched.", cityService.getCityById(cityId)));
	}

	// ── ADMIN ONLY ────────────────────────────────────

	@GetMapping("/api/v1/admin/cities")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — get all cities")
	public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities() {

		return ResponseEntity.ok(ApiResponse.success("All cities fetched.", cityService.getAllCities()));
	}

	@PostMapping("/api/v1/admin/cities")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — create new city")
	public ResponseEntity<ApiResponse<CityResponse>> createCity(@Valid @RequestBody CreateCityRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("City created successfully.", cityService.createCity(req)));
	}

	@PutMapping("/api/v1/admin/cities/{cityId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — update city")
	public ResponseEntity<ApiResponse<CityResponse>> updateCity(@PathVariable Long cityId,
			@Valid @RequestBody UpdateCityRequest req) {

		return ResponseEntity.ok(ApiResponse.success("City updated.", cityService.updateCity(cityId, req)));
	}

	@GetMapping("/api/v1/admin/cities/{cityId}/config")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — get city config")
	public ResponseEntity<ApiResponse<CityConfigResponse>> getCityConfig(@PathVariable Long cityId) {

		return ResponseEntity.ok(ApiResponse.success("City config fetched.", cityService.getCityConfig(cityId)));
	}

	@PutMapping("/api/v1/admin/cities/{cityId}/config")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Admin — update city config")
	public ResponseEntity<ApiResponse<CityConfigResponse>> updateCityConfig(@PathVariable Long cityId,
			@Valid @RequestBody UpdateCityConfigRequest req) {

		return ResponseEntity
				.ok(ApiResponse.success("City config updated.", cityService.updateCityConfig(cityId, req)));
	}
}
