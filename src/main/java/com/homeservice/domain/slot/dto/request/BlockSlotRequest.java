package com.homeservice.domain.slot.dto.request;

import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class BlockSlotRequest {

	@NotNull(message = "City ID is required")
	@Min(value = 1)
	private Long cityId;

	@NotNull(message = "Service key is required")
	private ServiceKey serviceKey;

	@NotNull(message = "Date is required")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate slotDate;

	// start time in minutes from midnight
	// 7:00 AM = 420
	// 7:30 AM = 450
	// 8:00 AM = 480
	@NotNull(message = "Slot start minutes required")
	@Min(value = 0)
	private Integer slotStartMinutes;

	private String reason;
}
