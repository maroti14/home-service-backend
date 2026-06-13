package com.homeservice.domain.booking.controller;

import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.response.ApiResponse;
import com.homeservice.common.response.PagedResponse;
import com.homeservice.domain.booking.dto.response.BookingDetailResponse;
import com.homeservice.domain.booking.dto.response.BookingResponse;
import com.homeservice.domain.booking.repository.BookingRepository;
import com.homeservice.domain.booking.mapper.BookingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Booking Management")
public class AdminBookingController {

	private final BookingRepository bookingRepo;
	private final BookingMapper bookingMapper;

	@GetMapping
	@Operation(summary = "Get all bookings")
	public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>>

			getAllBookings(@RequestParam(required = false) BookingStatus status,
					@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

		PageRequest pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("createdAt").descending());

		var bookings = status != null ? bookingRepo.findByStatusOrderByCreatedAtDesc(status, pageable)
				: bookingRepo.findAllByOrderByCreatedAtDesc(pageable);

		return ResponseEntity.ok(
				ApiResponse.success("Bookings fetched.", PagedResponse.of(bookings.map(bookingMapper::toResponse))));
	}

	@GetMapping("/{bookingId}")
	@Operation(summary = "Get booking detail by ID")
	public ResponseEntity<ApiResponse<BookingDetailResponse>>

			getBookingDetail(@PathVariable @Min(1) Long bookingId) {

		var booking = bookingRepo.findById(bookingId)
				.orElseThrow(() -> new com.homeservice.common.exception.ResourceNotFoundException(
						"Booking not found: " + bookingId));

		return ResponseEntity.ok(ApiResponse.success("Booking fetched.", bookingMapper.toDetailResponse(booking)));
	}
}
