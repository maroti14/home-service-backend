package com.homeservice.domain.city.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CityResponse {

	private Long id;
	private String name;
	private String state;
	private String cityCode;
	private Boolean isActive;
	private LocalDateTime createdAt;
}
