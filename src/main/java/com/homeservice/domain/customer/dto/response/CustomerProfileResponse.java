package com.homeservice.domain.customer.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerProfileResponse {

	private Long id;
	private String name;
	private String email;
	private String mobile;
	private String city;
	private String profilePhotoUrl;
	private Boolean isActive;
	private Boolean isMobileVerified;
	private LocalDateTime createdAt;
}
