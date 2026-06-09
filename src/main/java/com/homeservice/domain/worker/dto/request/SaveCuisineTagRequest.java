package com.homeservice.domain.worker.dto.request;

import com.homeservice.common.enums.CuisineType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveCuisineTagRequest {

	@NotNull(message = "Cuisine type is required")
	private CuisineType cuisineType;
}
