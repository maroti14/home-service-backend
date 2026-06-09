package com.homeservice.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// shown to customers — limited info
@Getter
@Builder
public class WorkerPublicProfileResponse {

	private Long id;
	private String name;
	private String profilePhotoUrl;
	private Double rating;
	private Integer totalJobsCompleted;
	private String city;
	private List<String> serviceTags;
	private List<String> cuisineTags;
}