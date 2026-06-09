package com.homeservice.domain.worker.dto.request;

import com.homeservice.common.enums.DayOfWeek;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveAvailabilityRequest {

	@NotNull(message = "Availability slots required")
	private List<AvailabilitySlot> slots;

	@Getter
	@Setter
	public static class AvailabilitySlot {

		@NotNull(message = "Day of week is required")
		private DayOfWeek dayOfWeek;

		@NotNull
		@Min(value = 0, message = "Start hour min 0")
		@Max(value = 23, message = "Start hour max 23")
		private Integer startHour;

		@NotNull
		@Min(value = 1, message = "End hour min 1")
		@Max(value = 24, message = "End hour max 24")
		private Integer endHour;

		private Boolean isAvailable = true;
	}
}