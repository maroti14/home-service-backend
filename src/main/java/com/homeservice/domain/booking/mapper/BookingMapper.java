package com.homeservice.domain.booking.mapper;

import com.homeservice.domain.booking.dto.response.BookingDetailResponse;
import com.homeservice.domain.booking.dto.response.BookingResponse;
import com.homeservice.domain.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "city.name", target = "cityName")
	@Mapping(source = "customer.id", target = "customerId")
	@Mapping(source = "customer.name", target = "customerName")
	@Mapping(source = "customer.mobile", target = "customerMobile")
	@Mapping(source = "worker.id", target = "workerId")
	@Mapping(source = "worker.name", target = "workerName")
	@Mapping(source = "worker.mobile", target = "workerMobile")
	@Mapping(source = "worker.rating", target = "workerRating")
	BookingDetailResponse toDetailResponse(Booking booking);

	@Mapping(source = "worker.id", target = "workerId")
	@Mapping(source = "worker.name", target = "workerName")
	@Mapping(source = "worker.rating", target = "workerRating")
	@Mapping(source = "worker.profilePhotoUrl", target = "workerPhotoUrl")
	BookingResponse toResponse(Booking booking);
}
