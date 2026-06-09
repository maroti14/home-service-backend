package com.homeservice.domain.slot.controller;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.common.response.ApiResponse;
import com.homeservice.domain.slot.dto.response.SlotGridResponse;
import com.homeservice.domain.slot.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
@Validated
@Tag(name = "Slot Availability")
public class SlotController {

	private final SlotService slotService;

	@GetMapping("/availability")
	@Operation(summary = "Get slot availability", description = "Get 24 slots for a specific "
			+ "date, city and service. " + "Results are cached for 60 seconds.")
	public ResponseEntity<ApiResponse<SlotGridResponse>>

			getAvailability(
					@RequestParam @NotNull(message = "City ID required") @Min(value = 1, message = "Invalid city ID") Long cityId,

					@RequestParam @NotNull(message = "Service required") ServiceKey serviceKey,

					@RequestParam @NotNull(message = "Date required") @FutureOrPresent(message = "Date must be "
							+ "today or future") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return ResponseEntity
				.ok(ApiResponse.success("Slots fetched.", slotService.getSlotGrid(cityId, serviceKey, date)));
	}

	@GetMapping("/availability/week")
	@Operation(summary = "Get slot availability for multiple days", description = "Get slots for up to 7 days "
			+ "starting from the given date.")
	public ResponseEntity<ApiResponse<List<SlotGridResponse>>>

			getWeekAvailability(@RequestParam @NotNull @Min(1) Long cityId,

					@RequestParam @NotNull ServiceKey serviceKey,

					@RequestParam @NotNull @FutureOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

					@RequestParam(defaultValue = "7") @Min(value = 1, message = "Min 1 day") @Max(value = 7, message = "Max 7 days") int days) {

		return ResponseEntity.ok(ApiResponse.success("Slot availability fetched.",
				slotService.getSlotGridForDays(cityId, serviceKey, fromDate, days)));
	}
}
