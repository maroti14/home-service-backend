package com.homeservice.domain.booking.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.enums.CancellationReason;
import com.homeservice.common.enums.PaymentStatus;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.common.response.PagedResponse;
import com.homeservice.config.AppProperties;
import com.homeservice.domain.booking.dto.request.CancelBookingRequest;
import com.homeservice.domain.booking.dto.request.CreateBookingRequest;
import com.homeservice.domain.booking.dto.request.RescheduleBookingRequest;
import com.homeservice.domain.booking.dto.response.BookingDetailResponse;
import com.homeservice.domain.booking.dto.response.BookingResponse;
import com.homeservice.domain.booking.dto.response.PriceBreakdownResponse;
import com.homeservice.domain.booking.dto.response.RazorpayOrderResponse;
import com.homeservice.domain.booking.entity.Booking;
import com.homeservice.domain.booking.mapper.BookingMapper;
import com.homeservice.domain.booking.repository.BookingRepository;
import com.homeservice.domain.city.entity.City;
import com.homeservice.domain.city.repository.CityRepository;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.domain.slot.service.SlotCacheService;
import com.homeservice.domain.slot.service.SlotLockService;
import com.homeservice.infrastructure.payment.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepo;
	private final CustomerRepository customerRepo;
	private final CityRepository cityRepo;
	private final PricingService pricingService;
	private final SlotLockService slotLockService;
	private final SlotCacheService slotCacheService;
	private final RazorpayService razorpayService;
	private final BookingMapper bookingMapper;
	private final ObjectMapper objectMapper;
	private final AppProperties props;
	private final ApplicationEventPublisher eventPublisher;

	// ── Get price estimate ────────────────────────────

	@Override
	@Transactional(readOnly = true)
	public PriceBreakdownResponse getPrice(CreateBookingRequest req) {
		return pricingService.calculate(req);
	}

	// ── Create booking ────────────────────────────────

	@Override
	@Transactional
	public RazorpayOrderResponse createBooking(Long customerId, CreateBookingRequest req) {

		// 1. validate city exists
		City city = findCity(req.getCityId());

		// 2. validate customer exists
		Customer customer = findCustomer(customerId);

		// 3. validate slot date not in past
		if (req.getSlotDate().isBefore(LocalDate.now())) {
			throw new InvalidInputException("Slot date cannot be in the past.");
		}

		// 4. check slot not already booked
		if (bookingRepo.isSlotBooked(req.getCityId(), req.getServiceKey(), req.getSlotDate(),
				req.getSlotStartMinutes())) {
			throw new InvalidInputException("This slot is already booked. " + "Please select another time.");
		}

		// 5. check slot not blocked by admin
		// (slot lock service handles this)
		String tempRef = "PENDING_" + customerId + "_" + System.currentTimeMillis();

		boolean locked = slotLockService.acquireLock(req.getCityId(), req.getServiceKey(), req.getSlotDate(),
				req.getSlotStartMinutes(), tempRef);

		if (!locked) {
			throw new InvalidInputException("This slot is currently " + "being booked by someone else. "
					+ "Please try again or pick " + "another slot.");
		}

		// 6. calculate price
		PriceBreakdownResponse price = pricingService.calculate(req);

		// 7. build service details map
		Map<String, Object> serviceDetails = buildServiceDetails(req);

		// 8. calculate slot times
		LocalTime slotStart = minutesToTime(req.getSlotStartMinutes());
		int endMinutes = req.getSlotStartMinutes() + (req.getTotalSlots() * 30);
		LocalTime slotEnd = minutesToTime(endMinutes);

		// 9. create booking record
		Booking booking = Booking.builder().customer(customer).city(city).serviceKey(req.getServiceKey())
				.status(BookingStatus.PENDING).slotDate(req.getSlotDate()).slotStartTime(slotStart).slotEndTime(slotEnd)
				.slotStartMinutes(req.getSlotStartMinutes()).totalSlots(req.getTotalSlots())
				.customerAddress(req.getCustomerAddress()).customerLatitude(req.getCustomerLatitude())
				.customerLongitude(req.getCustomerLongitude()).totalAmount(price.getTotalAmount())
				.baseAmount(price.getBasePrice().add(price.getExtraSlotsPrice()).add(price.getAddOnsPrice()))
				.platformFee(price.getPlatformFee()).workerEarnings(price.getWorkerEarnings())
				.paymentStatus(PaymentStatus.PENDING).paymentMethod(req.getPaymentMethod())
				.serviceDetails(serviceDetails).build();

		bookingRepo.save(booking);

		// 10. create Razorpay order
		String razorpayOrderId = razorpayService.createOrder(price.getTotalAmount(), "INR",
				"BOOKING_" + booking.getId());

		booking.setRazorpayOrderId(razorpayOrderId);
		bookingRepo.save(booking);

		log.info("Booking created | " + "bookingId={} service={} " + "amount={}", booking.getId(), req.getServiceKey(),
				price.getTotalAmount());

		return RazorpayOrderResponse.builder().bookingId(booking.getId()).razorpayOrderId(razorpayOrderId)
				.amount(price.getTotalAmount()).currency("INR").keyId(props.getRazorpay().getKeyId()).build();
	}

	// ── Confirm payment ───────────────────────────────

	@Override
	@Transactional
	public BookingDetailResponse confirmPayment(String razorpayOrderId, String razorpayPaymentId,
			String razorpaySignature) {

		Booking booking = bookingRepo.findByRazorpayOrderId(razorpayOrderId).orElseThrow(
				() -> new ResourceNotFoundException("Booking not found for " + "order: " + razorpayOrderId));

		// idempotency check
		// if already confirmed do not process again
		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			log.warn("Duplicate payment webhook | " + "orderId={}", razorpayOrderId);
			return bookingMapper.toDetailResponse(booking);
		}

		// verify payment signature
		// (skip verification if Razorpay disabled)
		if (props.getRazorpay().isEnabled()) {
			boolean valid = razorpayService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId,
					razorpaySignature);

			if (!valid) {
				// release slot lock on failure
				slotLockService.releaseLock(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate(),
						booking.getSlotStartMinutes());

				booking.setStatus(BookingStatus.CANCELLED);
				booking.setPaymentStatus(PaymentStatus.FAILED);
				booking.setCancellationReason(CancellationReason.PAYMENT_FAILED);
				bookingRepo.save(booking);

				throw new InvalidInputException("Payment verification failed.");
			}
		}

		// update booking status
		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setPaymentStatus(PaymentStatus.CAPTURED);
		booking.setRazorpayPaymentId(razorpayPaymentId);
		bookingRepo.save(booking);

		// persist slot lock permanently
		slotLockService.persistLock(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate(),
				booking.getSlotStartMinutes());

		// invalidate slot cache
		slotCacheService.invalidate(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate());

		log.info("Payment confirmed | " + "bookingId={} orderId={}", booking.getId(), razorpayOrderId);

		// publish event for dispatch module
		// (Module 8 will handle this)
		// eventPublisher.publishEvent(
		// new BookingConfirmedEvent(booking));

		return bookingMapper.toDetailResponse(booking);
	}

	// ── Get booking detail ────────────────────────────

	@Override
	@Transactional(readOnly = true)
	public BookingDetailResponse getBookingDetail(Long bookingId, Long customerId) {

		Booking booking = findBooking(bookingId);

		// verify ownership
		if (!booking.getCustomer().getId().equals(customerId)) {
			throw new ResourceNotFoundException("Booking not found: " + bookingId);
		}

		return bookingMapper.toDetailResponse(booking);
	}

	// ── Customer booking history ──────────────────────

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<BookingResponse> getCustomerBookings(Long customerId, BookingStatus status,
			Pageable pageable) {

		Page<Booking> page;

		if (status != null) {
			page = bookingRepo.findByCustomerIdAndStatusOrderByCreatedAtDesc(customerId, status, pageable);
		} else {
			page = bookingRepo.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
		}

		return PagedResponse.of(page.map(bookingMapper::toResponse));
	}

	// ── Reschedule ────────────────────────────────────

	@Override
	@Transactional
	public BookingDetailResponse reschedule(Long bookingId, Long customerId, RescheduleBookingRequest req) {

		Booking booking = findBooking(bookingId);

		// verify ownership
		if (!booking.getCustomer().getId().equals(customerId)) {
			throw new ResourceNotFoundException("Booking not found: " + bookingId);
		}

		// only CONFIRMED bookings can be
		// rescheduled
		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new InvalidInputException("Only confirmed bookings " + "can be rescheduled.");
		}

		// check new slot not already booked
		if (bookingRepo.isSlotBooked(booking.getCity().getId(), booking.getServiceKey(), req.getNewSlotDate(),
				req.getNewSlotStartMinutes())) {
			throw new InvalidInputException("New slot is already booked. " + "Please choose another time.");
		}

		// release old slot lock
		slotLockService.releaseLock(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate(),
				booking.getSlotStartMinutes());

		// invalidate old slot cache
		slotCacheService.invalidate(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate());

		// acquire new slot lock
		boolean locked = slotLockService.acquireLock(booking.getCity().getId(), booking.getServiceKey(),
				req.getNewSlotDate(), req.getNewSlotStartMinutes(), "CONFIRMED:" + bookingId);

		if (!locked) {
			throw new InvalidInputException("New slot is being booked " + "by someone else. Try again.");
		}

		// update booking
		LocalTime newStart = minutesToTime(req.getNewSlotStartMinutes());
		int newEndMinutes = req.getNewSlotStartMinutes() + (booking.getTotalSlots() * 30);
		LocalTime newEnd = minutesToTime(newEndMinutes);

		booking.setSlotDate(req.getNewSlotDate());
		booking.setSlotStartTime(newStart);
		booking.setSlotEndTime(newEnd);
		booking.setSlotStartMinutes(req.getNewSlotStartMinutes());

		bookingRepo.save(booking);

		// invalidate new slot cache
		slotCacheService.invalidate(booking.getCity().getId(), booking.getServiceKey(), req.getNewSlotDate());

		log.info("Booking rescheduled | " + "bookingId={} newDate={}", bookingId, req.getNewSlotDate());

		return bookingMapper.toDetailResponse(booking);
	}

	// ── Cancel ────────────────────────────────────────

	@Override
	@Transactional
	public BookingDetailResponse cancel(Long bookingId, Long customerId, CancelBookingRequest req) {

		Booking booking = findBooking(bookingId);

		// verify ownership
		if (!booking.getCustomer().getId().equals(customerId)) {
			throw new ResourceNotFoundException("Booking not found: " + bookingId);
		}

		// cannot cancel already cancelled/completed
		if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
			throw new InvalidInputException("This booking cannot be cancelled.");
		}

		// cannot cancel if worker already arrived
		if (booking.getStatus() == BookingStatus.WORKER_ARRIVED || booking.getStatus() == BookingStatus.IN_PROGRESS) {
			throw new InvalidInputException("Cannot cancel after worker " + "has arrived.");
		}

		// calculate cancellation fee
		BigDecimal cancelFee = BigDecimal.ZERO;

		if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.WORKER_ASSIGNED) {

			LocalDateTime slotDateTime = booking.getSlotDate().atTime(booking.getSlotStartTime());

			long minutesUntilSlot = java.time.Duration.between(LocalDateTime.now(), slotDateTime).toMinutes();

			if (minutesUntilSlot < props.getBooking().getCancelFreeMinutes()) {
				cancelFee = BigDecimal.valueOf(props.getBooking().getCancelFee());
			}
		}

		// release slot lock
		if (booking.getStatus() != BookingStatus.PENDING) {
			slotLockService.releaseLock(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate(),
					booking.getSlotStartMinutes());

			slotCacheService.invalidate(booking.getCity().getId(), booking.getServiceKey(), booking.getSlotDate());
		}

		// update booking
		booking.setStatus(BookingStatus.CANCELLED);
		booking.setCancellationReason(CancellationReason.CUSTOMER_CANCELLED);
		booking.setCancellationNote(req.getReason());
		booking.setCancelledAt(LocalDateTime.now());
		booking.setCancellationFee(cancelFee);
		bookingRepo.save(booking);

		log.info("Booking cancelled | " + "bookingId={} fee={}", bookingId, cancelFee);

		return bookingMapper.toDetailResponse(booking);
	}

	// ── Worker: today jobs ────────────────────────────

	@Override
	@Transactional(readOnly = true)
	public List<BookingResponse> getWorkerTodayJobs(Long workerId) {

		return bookingRepo.findWorkerTodaysJobs(workerId, LocalDate.now()).stream().map(bookingMapper::toResponse)
				.collect(Collectors.toList());
	}

	// ── Worker: job history ───────────────────────────

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<BookingResponse> getWorkerBookings(Long workerId, Pageable pageable) {

		Page<Booking> page = bookingRepo.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable);

		return PagedResponse.of(page.map(bookingMapper::toResponse));
	}

	private Map<String, Object> buildServiceDetails(CreateBookingRequest req) {

	    Object detail = switch (req.getServiceKey()) {
	        case COOKING -> req.getCookingDetail();
	        case BIKE_WASH -> req.getBikeWashDetail();
	        case CLEANING -> req.getCleaningDetail();
	        case BATHROOM_CLEANING -> req.getBathroomDetail();
	        case MAID -> req.getMaidDetail();
	        case KITCHEN_PREP -> req.getKitchenPrepDetail();
	        case DUSTING_WIPING -> req.getDustingDetail();
	        case DISHWASHING -> req.getDishwashingDetail();
	    };

	    if (detail == null) {
	        return null;
	    }

	    return objectMapper.convertValue(
	            detail,
	            new TypeReference<Map<String, Object>>() {});
	}
	private LocalTime minutesToTime(int minutes) {
		return LocalTime.of(minutes / 60, minutes % 60);
	}

	private Booking findBooking(Long id) {
		return bookingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
	}

	private Customer findCustomer(Long id) {
		return customerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
	}

	private City findCity(Long id) {
		return cityRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
	}
}
