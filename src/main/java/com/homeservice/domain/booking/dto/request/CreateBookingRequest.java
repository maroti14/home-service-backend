package com.homeservice.domain.booking.dto.request;

import com.homeservice.common.enums.PaymentMethod;
import com.homeservice.common.enums.ServiceKey;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class CreateBookingRequest {

	@NotNull(message = "City ID required")
	@Min(value = 1)
	private Long cityId;

	@NotNull(message = "Service key required")
	private ServiceKey serviceKey;

	// ── Slot ──────────────────────────────────────────
	@NotNull(message = "Slot date required")
	@FutureOrPresent(message = "Slot date must be today or future")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate slotDate;

	// slot start in minutes from midnight
	// 7:00 AM = 420
	@NotNull(message = "Slot start required")
	@Min(value = 420, message = "Min slot is 7:00 AM (420)")
	private Integer slotStartMinutes;

	// how many 30-min slots to book
	@NotNull(message = "Total slots required")
	@Min(value = 1, message = "Min 1 slot")
	private Integer totalSlots;

	// ── Customer address ──────────────────────────────
	@NotBlank(message = "Address required")
	@Size(max = 500)
	private String customerAddress;

	@NotNull(message = "Latitude required")
	@DecimalMin(value = "-90.0")
	@DecimalMax(value = "90.0")
	private Double customerLatitude;

	@NotNull(message = "Longitude required")
	@DecimalMin(value = "-180.0")
	@DecimalMax(value = "180.0")
	private Double customerLongitude;

	// ── Payment ───────────────────────────────────────
	@NotNull(message = "Payment method required")
	private PaymentMethod paymentMethod;

	// ── Service-specific details ──────────────────────
	// Only one should be provided
	// based on the serviceKey

	private CookingDetailRequest cookingDetail;
	private BikeWashDetailRequest bikeWashDetail;
	private CleaningDetailRequest cleaningDetail;
	private BathroomDetailRequest bathroomDetail;
	private MaidDetailRequest maidDetail;
	private KitchenPrepDetailRequest kitchenPrepDetail;
	private DustingDetailRequest dustingDetail;
	private DishwashingDetailRequest dishwashingDetail;
}