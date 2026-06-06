package com.homeservice.domain.servicetype.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlotConfigResponse {

	private Long id;
	private Long cityId;
	private String cityName;
	private Long serviceTypeId;
	private String serviceTypeName;
	private Integer startHour;
	private Integer endHour;
	private Integer slotIntervalMinutes;
	private Integer advanceBookingDays;
	private Integer totalSlotsPerDay;
	private Boolean isActive;
}
