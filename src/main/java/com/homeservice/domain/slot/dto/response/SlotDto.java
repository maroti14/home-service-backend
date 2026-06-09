package com.homeservice.domain.slot.dto.response;

import com.homeservice.common.enums.SlotStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SlotDto {

	// slot date
	private LocalDate date;

	// slot start time as HH:mm
	// e.g. "07:00", "07:30"
	private String startTime;

	// slot end time as HH:mm
	// e.g. "07:30", "08:00"
	private String endTime;

	// start time in minutes from midnight
	// used for lock/unlock operations
	private Integer startMinutes;

	// AVAILABLE / LOCKED / BOOKED / BLOCKED
	private SlotStatus status;

	// how many bookings in this slot
	// used for heatmap
	private int bookingCount;
}
