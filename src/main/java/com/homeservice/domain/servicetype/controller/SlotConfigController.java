package com.homeservice.domain.servicetype.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.servicetype.dto.request.UpdateServicePricingRequest;
import com.homeservice.domain.servicetype.dto.request.UpdateSlotConfigRequest;
import com.homeservice.domain.servicetype.dto.response.ServicePricingResponse;
import com.homeservice.domain.servicetype.dto.response.SlotConfigResponse;
import com.homeservice.domain.servicetype.service.ServicePricingService;
import com.homeservice.domain.servicetype.service.SlotConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Slot Config & Pricing (Admin)")
public class SlotConfigController {

	private final SlotConfigService slotConfigService;
	private final ServicePricingService pricingService;

	@GetMapping("/api/v1/admin/slot-config/{cityId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all slot configs for city")
	public ResponseEntity<ApiResponse<List<SlotConfigResponse>>> getSlotConfigs(@PathVariable Long cityId) {

		return ResponseEntity.ok(ApiResponse.success("Slot configs fetched.", slotConfigService.getByCity(cityId)));
	}

	@PutMapping("/api/v1/admin/slot-config/{configId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update a slot config")
	public ResponseEntity<ApiResponse<SlotConfigResponse>> updateSlotConfig(@PathVariable Long configId,
			@Valid @RequestBody UpdateSlotConfigRequest req) {

		return ResponseEntity.ok(ApiResponse.success("Slot config updated.", slotConfigService.update(configId, req)));
	}

	@PutMapping("/api/v1/admin/pricing/{pricingId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update service pricing")
	public ResponseEntity<ApiResponse<ServicePricingResponse>> updatePricing(@PathVariable Long pricingId,
			@Valid @RequestBody UpdateServicePricingRequest req) {

		return ResponseEntity.ok(ApiResponse.success("Pricing updated.", pricingService.update(pricingId, req)));
	}
}
