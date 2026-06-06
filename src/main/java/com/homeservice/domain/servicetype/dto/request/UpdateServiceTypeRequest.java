package com.homeservice.domain.servicetype.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateServiceTypeRequest {

	private String name;
	private String description;
	private String iconName;
	private String colorCode;
	private String bgColorCode;
	private Integer minSlots;
	private Integer maxSlots;
	private Boolean requiresCuisineTag;
	private Boolean requiresPhotos;
	private Boolean isNew;
	private Boolean isActive;
	private Integer displayOrder;
}
