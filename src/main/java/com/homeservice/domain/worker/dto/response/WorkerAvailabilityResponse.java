package com.homeservice.domain.worker.dto.response;

import com.homeservice.common.enums.DayOfWeek;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerAvailabilityResponse {

	private Long id;
	private DayOfWeek dayOfWeek;
	private Integer startHour;
	private Integer endHour;
	private Boolean isAvailable;
}
