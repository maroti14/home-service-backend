package com.homeservice.domain.slot.dto.request;

import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class SlotAvailabilityRequest {

	@NotNull(message = "City ID is required")
	@Min(value = 1, message = "City ID must be positive")
	private Long cityId;

	@NotNull(message = "Service key is required")
	private ServiceKey serviceKey;

	@NotNull(message = "Date is required")
	@FutureOrPresent(message = "Date must be today or future")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate date;

	// how many days to fetch (default 1, max 7)
	@Min(value = 1, message = "Days must be at least 1")
	private int days = 1;
}
