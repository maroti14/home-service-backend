package com.homeservice.domain.booking.service;

import com.homeservice.common.enums.BookingStatus;
import com.homeservice.domain.booking.dto.request.CancelBookingRequest;
import com.homeservice.domain.booking.dto.request.CreateBookingRequest;
import com.homeservice.domain.booking.dto.request.RescheduleBookingRequest;
import com.homeservice.domain.booking.dto.response.BookingDetailResponse;
import com.homeservice.domain.booking.dto.response.BookingResponse;
import com.homeservice.domain.booking.dto.response.PriceBreakdownResponse;
import com.homeservice.domain.booking.dto.response.RazorpayOrderResponse;
import com.homeservice.common.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {

	// step 1: get price before booking
	PriceBreakdownResponse getPrice(CreateBookingRequest req);

	// step 2: create booking and get
	// Razorpay order ID
	RazorpayOrderResponse createBooking(Long customerId, CreateBookingRequest req);

	// step 3: called after payment success
	BookingDetailResponse confirmPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);

	// customer: get booking details
	BookingDetailResponse getBookingDetail(Long bookingId, Long customerId);

	// customer: get booking history
	PagedResponse<BookingResponse> getCustomerBookings(Long customerId, BookingStatus status, Pageable pageable);

	// customer: reschedule
	BookingDetailResponse reschedule(Long bookingId, Long customerId, RescheduleBookingRequest req);

	// customer: cancel
	BookingDetailResponse cancel(Long bookingId, Long customerId, CancelBookingRequest req);

	// worker: get today jobs
	java.util.List<BookingResponse> getWorkerTodayJobs(Long workerId);

	// worker: get job history
	PagedResponse<BookingResponse> getWorkerBookings(Long workerId, Pageable pageable);
}
