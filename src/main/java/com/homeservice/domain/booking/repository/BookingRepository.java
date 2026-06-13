package com.homeservice.domain.booking.repository;

import com.homeservice.common.enums.BookingStatus;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	// customer booking history
	Page<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

	// customer booking history filtered by status
	Page<Booking> findByCustomerIdAndStatusOrderByCreatedAtDesc(Long customerId, BookingStatus status,
			Pageable pageable);

	// worker job history
	Page<Booking> findByWorkerIdOrderByCreatedAtDesc(Long workerId, Pageable pageable);

	// find by razorpay order ID
	Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);

	// check slot already booked
	@Query("""
			SELECT COUNT(b) > 0 FROM Booking b
			WHERE b.city.id = :cityId
			AND b.serviceKey = :serviceKey
			AND b.slotDate = :slotDate
			AND b.slotStartMinutes = :slotStartMinutes
			AND b.status NOT IN (
			    com.homeservice.common.enums
			    .BookingStatus.CANCELLED,
			    com.homeservice.common.enums
			    .BookingStatus.PENDING)
			""")
	boolean isSlotBooked(Long cityId, ServiceKey serviceKey, LocalDate slotDate, Integer slotStartMinutes);

	// admin: get all bookings paginated
	Page<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);

	// admin: filter by status
	Page<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status, Pageable pageable);

	// admin: filter by city
	Page<Booking> findByCityIdOrderByCreatedAtDesc(Long cityId, Pageable pageable);

	// jobs for today
	@Query("""
			SELECT b FROM Booking b
			WHERE b.slotDate = :today
			AND b.status NOT IN (
			    com.homeservice.common.enums
			    .BookingStatus.CANCELLED)
			ORDER BY b.slotStartTime ASC
			""")
	List<Booking> findTodaysJobs(LocalDate today);

	// worker today jobs
	@Query("""
			SELECT b FROM Booking b
			WHERE b.worker.id = :workerId
			AND b.slotDate = :today
			AND b.status NOT IN (
			    com.homeservice.common.enums
			    .BookingStatus.CANCELLED,
			    com.homeservice.common.enums
			    .BookingStatus.PENDING)
			ORDER BY b.slotStartTime ASC
			""")
	List<Booking> findWorkerTodaysJobs(Long workerId, LocalDate today);

	// bookings for analytics
	@Query("""
			SELECT COUNT(b) FROM Booking b
			WHERE b.city.id = :cityId
			AND b.slotDate = :date
			AND b.status = :status
			""")
	long countByCityDateStatus(Long cityId, LocalDate date, BookingStatus status);
}
