package com.homeservice.domain.worker.dto.request;

import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveServiceTagRequest {

	@NotNull(message = "Service key is required")
	private ServiceKey serviceKey;
}
