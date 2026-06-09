package com.homeservice.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkerProfileResponse {

	private Long id;
	private String name;
	private String email;
	private String mobile;
	private String city;
	private String bio;
	private String profilePhotoUrl;
	private Integer experienceYears;
	private Double rating;
	private Integer totalRatings;
	private Integer totalJobsCompleted;
	private Double acceptanceRate;
	private Boolean isApproved;
	private Boolean isOnline;
	private Boolean isActive;
	private Boolean isMobileVerified;
	private LocalDateTime createdAt;
}
