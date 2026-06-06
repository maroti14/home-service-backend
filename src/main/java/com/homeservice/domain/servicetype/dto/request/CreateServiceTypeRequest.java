package com.homeservice.domain.servicetype.dto.request;

import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceTypeRequest {

	@NotNull(message = "Service key is required")
	private ServiceKey serviceKey;

	@NotBlank(message = "Name is required")
	private String name;

	private String description;

	private String iconName;

	private String colorCode;

	private String bgColorCode;

	@Min(value = 1, message = "Min slots must be at least 1")
	private Integer minSlots;

	@Min(value = 1, message = "Max slots must be at least 1")
	private Integer maxSlots;

	private Boolean requiresCuisineTag;

	private Boolean requiresPhotos;

	private Boolean isNew;

	private Integer displayOrder;
}
