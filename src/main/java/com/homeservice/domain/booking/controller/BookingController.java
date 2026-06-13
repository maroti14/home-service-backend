package com.homeservice.domain.booking.controller;

import com.homeservice.common.constant.ApiConstants;
import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.response.ApiResponse;
import com.homeservice.common.response.PagedResponse;
import com.homeservice.domain.booking.dto.request.CancelBookingRequest;
import com.homeservice.domain.booking.dto.request.CreateBookingRequest;
import com.homeservice.domain.booking.dto.request.RescheduleBookingRequest;
import com.homeservice.domain.booking.dto.response.BookingDetailResponse;
import com.homeservice.domain.booking.dto.response.BookingResponse;
import com.homeservice.domain.booking.dto.response.PriceBreakdownResponse;
import com.homeservice.domain.booking.dto.response.RazorpayOrderResponse;
import com.homeservice.domain.booking.service.BookingService;
import com.homeservice.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Booking")
public class BookingController {

	private final BookingService bookingService;

	// ── Price estimate (before booking) ───────────────

	@PostMapping("/price")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Get price estimate", description = "Get price breakdown before " + "placing a booking. "
			+ "No payment or slot lock at this step.")
	public ResponseEntity<ApiResponse<PriceBreakdownResponse>>

			getPrice(@Valid @RequestBody CreateBookingRequest req) {

		return ResponseEntity.ok(ApiResponse.success("Price calculated.", bookingService.getPrice(req)));
	}

	// ── Create booking ────────────────────────────────

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Create a booking", description = "Creates booking and returns " + "Razorpay order ID. "
			+ "Flutter app must complete payment " + "using this order ID.")
	public ResponseEntity<ApiResponse<RazorpayOrderResponse>>

			createBooking(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody CreateBookingRequest req) {

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Booking created. Complete payment.",
				bookingService.createBooking(currentUser.getUserId(), req)));
	}

	// ── Confirm payment (webhook or manual) ───────────

	@PostMapping("/confirm-payment")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Confirm payment", description = "Call after Razorpay payment " + "is successful. In dev mode "
			+ "signature is not verified.")
	public ResponseEntity<ApiResponse<BookingDetailResponse>>

			confirmPayment(@RequestParam String razorpayOrderId, @RequestParam String razorpayPaymentId,
					@RequestParam(defaultValue = "dev_signature") String razorpaySignature) {

		return ResponseEntity.ok(ApiResponse.success("Payment confirmed. " + "Finding worker...",
				bookingService.confirmPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature)));
	}

	// ── Get booking detail ────────────────────────────

	@GetMapping("/{bookingId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Get booking details")
	public ResponseEntity<ApiResponse<BookingDetailResponse>>

			getDetail(@PathVariable @Min(value = 1) Long bookingId,
					@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity.ok(ApiResponse.success("Booking fetched.",
				bookingService.getBookingDetail(bookingId, currentUser.getUserId())));
	}

	// ── Customer booking history ──────────────────────

	@GetMapping("/my-bookings")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "My booking history", description = "Filter by status: PENDING, "
			+ "CONFIRMED, COMPLETED, CANCELLED. " + "Leave empty for all.")
	public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>>

			myBookings(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestParam(required = false) BookingStatus status, @RequestParam(defaultValue = "0") int page,
					@RequestParam(defaultValue = "10") int size) {

		PageRequest pageable = PageRequest.of(page, Math.min(size, ApiConstants.MAX_PAGE_SIZE),
				Sort.by("createdAt").descending());

		return ResponseEntity.ok(ApiResponse.success("Bookings fetched.",
				bookingService.getCustomerBookings(currentUser.getUserId(), status, pageable)));
	}

	// ── Reschedule ────────────────────────────────────

	@PutMapping("/{bookingId}/reschedule")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Reschedule a booking", description = "Only CONFIRMED bookings can " + "be rescheduled.")
	public ResponseEntity<ApiResponse<BookingDetailResponse>>

			reschedule(@PathVariable @Min(1) Long bookingId, @AuthenticationPrincipal UserDetailsImpl currentUser,
					@Valid @RequestBody RescheduleBookingRequest req) {

		return ResponseEntity.ok(ApiResponse.success("Booking rescheduled.",
				bookingService.reschedule(bookingId, currentUser.getUserId(), req)));
	}

	// ── Cancel ────────────────────────────────────────

	@DeleteMapping("/{bookingId}/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Cancel a booking", description = "Free if > 30 min before slot. "
			+ "₹50 fee if cancelled late.")
	public ResponseEntity<ApiResponse<BookingDetailResponse>>

			cancel(@PathVariable @Min(1) Long bookingId, @AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestBody(required = false) CancelBookingRequest req) {

		if (req == null) {
			req = new CancelBookingRequest();
		}

		return ResponseEntity.ok(ApiResponse.success("Booking cancelled.",
				bookingService.cancel(bookingId, currentUser.getUserId(), req)));
	}

	// ── Worker: today jobs ────────────────────────────

	@GetMapping("/worker/today")
	@PreAuthorize("hasRole('WORKER')")
	@Operation(summary = "Worker today's jobs")
	public ResponseEntity<ApiResponse<List<BookingResponse>>>

			workerTodayJobs(@AuthenticationPrincipal UserDetailsImpl currentUser) {

		return ResponseEntity.ok(ApiResponse.success("Today's jobs fetched.",
				bookingService.getWorkerTodayJobs(currentUser.getUserId())));
	}

	// ── Worker: job history ───────────────────────────

	@GetMapping("/worker/history")
	@PreAuthorize("hasRole('WORKER')")
	@Operation(summary = "Worker job history")
	public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>>

			workerJobHistory(@AuthenticationPrincipal UserDetailsImpl currentUser,
					@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		PageRequest pageable = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());

		return ResponseEntity.ok(ApiResponse.success("Job history fetched.",
				bookingService.getWorkerBookings(currentUser.getUserId(), pageable)));
	}
}
