package com.homeservice.domain.worker.dto.response;

import com.homeservice.common.enums.CuisineType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerCuisineTagResponse {

	private Long id;
	private CuisineType cuisineType;
}
