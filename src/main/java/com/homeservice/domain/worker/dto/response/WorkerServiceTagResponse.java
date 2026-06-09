package com.homeservice.domain.worker.dto.response;

import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerServiceTagResponse {

	private Long id;
	private ServiceKey serviceKey;
	private String serviceName;
	private Boolean isApproved;
}
