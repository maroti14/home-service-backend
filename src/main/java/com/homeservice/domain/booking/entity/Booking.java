package com.homeservice.domain.booking.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.enums.CancellationReason;
import com.homeservice.common.enums.PaymentMethod;
import com.homeservice.common.enums.PaymentStatus;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.city.entity.City;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "bookings", indexes = { @Index(name = "idx_booking_customer", columnList = "customer_id"),
		@Index(name = "idx_booking_worker", columnList = "worker_id"),
		@Index(name = "idx_booking_status", columnList = "status"),
		@Index(name = "idx_booking_slot_date", columnList = "slot_date"),
		@Index(name = "idx_booking_city", columnList = "city_id"),
		@Index(name = "idx_booking_razorpay", columnList = "razorpay_order_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id")
	private Worker worker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ServiceKey serviceKey;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(nullable = false, length = 30)
	private BookingStatus status = BookingStatus.PENDING;

	// ── Slot info ─────────────────────────────────────
	@Column(nullable = false)
	private LocalDate slotDate;

	@Column(nullable = false)
	private LocalTime slotStartTime;

	@Column(nullable = false)
	private LocalTime slotEndTime;

	// start time in minutes from midnight
	// used for slot lock/unlock
	@Column(nullable = false)
	private Integer slotStartMinutes;

	// how many 30-min slots booked
	@Column(nullable = false)
	private Integer totalSlots;

	// ── Customer address ──────────────────────────────
	@Column(nullable = false, length = 500)
	private String customerAddress;

	@Column(nullable = false)
	private Double customerLatitude;

	@Column(nullable = false)
	private Double customerLongitude;

	// ── Pricing ───────────────────────────────────────
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal baseAmount;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal platformFee;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal workerEarnings;

	// ── Payment ───────────────────────────────────────
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(nullable = false, length = 20)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private PaymentMethod paymentMethod;

	@Column(length = 100)
	private String razorpayOrderId;

	@Column(length = 100)
	private String razorpayPaymentId;

	// ── Service-specific details stored as JSON ───────
	// e.g. cuisine type, dishes, bike type etc.
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private Map<String, Object> serviceDetails;

	// ── Job timeline ──────────────────────────────────
	private LocalDateTime workerAssignedAt;
	private LocalDateTime workerAcceptedAt;
	private LocalDateTime workerArrivedAt;
	private LocalDateTime jobStartedAt;
	private LocalDateTime jobCompletedAt;
	private LocalDateTime customerConfirmedAt;

	// ── Cancellation ──────────────────────────────────
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private CancellationReason cancellationReason;

	@Column(length = 300)
	private String cancellationNote;

	private LocalDateTime cancelledAt;

	@Builder.Default
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal cancellationFee = BigDecimal.ZERO;
}
