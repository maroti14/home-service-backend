package com.homeservice.domain.booking.dto.response;

import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.enums.PaymentMethod;
import com.homeservice.common.enums.PaymentStatus;
import com.homeservice.common.enums.ServiceKey;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class BookingResponse {

	private Long id;
	private ServiceKey serviceKey;
	private String serviceName;
	private BookingStatus status;
	private PaymentStatus paymentStatus;
	private PaymentMethod paymentMethod;

	// slot info
	private LocalDate slotDate;
	private LocalTime slotStartTime;
	private LocalTime slotEndTime;
	private Integer totalSlots;

	// address
	private String customerAddress;

	// pricing
	private BigDecimal totalAmount;
	private BigDecimal platformFee;
	private BigDecimal workerEarnings;

	// worker info (shown after assignment)
	private Long workerId;
	private String workerName;
	private Double workerRating;
	private String workerPhotoUrl;

	// razorpay
	private String razorpayOrderId;

	private LocalDateTime createdAt;
}
