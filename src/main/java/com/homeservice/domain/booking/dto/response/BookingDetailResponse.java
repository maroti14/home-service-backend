package com.homeservice.domain.booking.dto.response;

import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.enums.CancellationReason;
import com.homeservice.common.enums.PaymentMethod;
import com.homeservice.common.enums.PaymentStatus;
import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Builder
public class BookingDetailResponse {

	private Long id;
	private Long cityId;
	private String cityName;
	private ServiceKey serviceKey;
	private String serviceName;
	private BookingStatus status;
	private PaymentStatus paymentStatus;
	private PaymentMethod paymentMethod;

	// slot
	private LocalDate slotDate;
	private LocalTime slotStartTime;
	private LocalTime slotEndTime;
	private Integer totalSlots;
	private Integer slotStartMinutes;

	// address
	private String customerAddress;
	private Double customerLatitude;
	private Double customerLongitude;

	// pricing
	private BigDecimal totalAmount;
	private BigDecimal baseAmount;
	private BigDecimal platformFee;
	private BigDecimal workerEarnings;
	private BigDecimal cancellationFee;

	// customer
	private Long customerId;
	private String customerName;
	private String customerMobile;

	// worker
	private Long workerId;
	private String workerName;
	private String workerMobile;
	private Double workerRating;

	// service specific details as json map
	private Map<String, Object> serviceDetails;

	// timeline
	private LocalDateTime workerAssignedAt;
	private LocalDateTime workerArrivedAt;
	private LocalDateTime jobStartedAt;
	private LocalDateTime jobCompletedAt;
	private LocalDateTime customerConfirmedAt;

	// cancellation
	private CancellationReason cancellationReason;
	private String cancellationNote;
	private LocalDateTime cancelledAt;

	private LocalDateTime createdAt;
}
