package com.homeservice.domain.slot.dto.request;

import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UnblockSlotRequest {

	@NotNull
	@Min(value = 1)
	private Long cityId;

	@NotNull
	private ServiceKey serviceKey;

	@NotNull
	private LocalDate slotDate;

	@NotNull
	@Min(value = 0)
	private Integer slotStartMinutes;
}
