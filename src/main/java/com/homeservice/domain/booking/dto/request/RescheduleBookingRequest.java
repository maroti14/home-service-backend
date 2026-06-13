package com.homeservice.domain.booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RescheduleBookingRequest {

	@NotNull(message = "New slot date required")
	@FutureOrPresent
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate newSlotDate;

	@NotNull(message = "New slot start required")
	@Min(value = 420)
	private Integer newSlotStartMinutes;
}