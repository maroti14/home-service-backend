package com.homeservice.domain.worker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkerOnlineStatusRequest {

	@NotNull(message = "Online status is required")
	private Boolean isOnline;
}
