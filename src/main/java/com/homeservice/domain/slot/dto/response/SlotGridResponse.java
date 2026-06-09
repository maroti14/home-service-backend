package com.homeservice.domain.slot.dto.response;

import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SlotGridResponse {

	private Long cityId;
	private String cityName;
	private ServiceKey serviceKey;
	private String serviceName;
	private LocalDate date;

	// all 24 slots for this date
	private List<SlotDto> slots;

	// total available slots
	private int availableCount;

	// total booked slots
	private int bookedCount;

	// total blocked slots
	private int blockedCount;
}
