package com.homeservice.domain.slot.controller;

import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.slot.dto.request.BlockSlotRequest;
import com.homeservice.domain.slot.dto.request.UnblockSlotRequest;
import com.homeservice.domain.slot.dto.response.SlotHeatmapResponse;
import com.homeservice.domain.slot.service.SlotService;
import com.homeservice.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/slots")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Slot Management")
public class AdminSlotController {

	private final SlotService slotService;

	@PostMapping("/block")
	@Operation(summary = "Block a slot", description = "Admin can block a specific slot " + "to prevent new bookings.")
	public ResponseEntity<ApiResponse<Void>> blockSlot(@Valid @RequestBody BlockSlotRequest req,
			@AuthenticationPrincipal UserDetailsImpl currentUser) {

		slotService.blockSlot(req, currentUser.getUsername());

		return ResponseEntity.ok(ApiResponse.success("Slot blocked successfully."));
	}

	@PostMapping("/unblock")
	@Operation(summary = "Unblock a slot")
	public ResponseEntity<ApiResponse<Void>> unblockSlot(@Valid @RequestBody UnblockSlotRequest req) {

		slotService.unblockSlot(req);

		return ResponseEntity.ok(ApiResponse.success("Slot unblocked successfully."));
	}

	@GetMapping("/heatmap")
	@Operation(summary = "Get slot demand heatmap", description = "Returns booking counts per "
			+ "slot time per service. " + "Used for admin dashboard.")
	public ResponseEntity<ApiResponse<SlotHeatmapResponse>>

			getHeatmap(@RequestParam @NotNull @Min(1) Long cityId,

					@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

					@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

		return ResponseEntity
				.ok(ApiResponse.success("Heatmap data fetched.", slotService.getHeatmap(cityId, fromDate, toDate)));
	}
}
