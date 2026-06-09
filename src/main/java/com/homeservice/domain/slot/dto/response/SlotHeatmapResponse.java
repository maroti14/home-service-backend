package com.homeservice.domain.slot.dto.response;

import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SlotHeatmapResponse {

	private Long cityId;
	private String cityName;
	private LocalDate fromDate;
	private LocalDate toDate;

	// Map<ServiceKey, Map<slotTime, bookingCount>>
	// e.g. CLEANING → { "07:00": 3, "07:30": 5 }
	private Map<ServiceKey, Map<String, Integer>> heatmapData;

	// peak slot across all services
	private String peakSlotTime;
	private int peakSlotCount;
}
