package com.homeservice.domain.servicetype.dto.response;

import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceTypeResponse {

	private Long id;
	private ServiceKey serviceKey;
	private String name;
	private String description;
	private String iconName;
	private String colorCode;
	private String bgColorCode;
	private Integer minSlots;
	private Integer maxSlots;
	private Integer slotDurationMinutes;
	private Boolean requiresCuisineTag;
	private Boolean requiresPhotos;
	private Boolean isNew;
	private Boolean isActive;
	private Integer displayOrder;
}
