package com.homeservice.domain.servicetype.service;

import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.servicetype.dto.request.UpdateSlotConfigRequest;
import com.homeservice.domain.servicetype.dto.response.SlotConfigResponse;
import com.homeservice.domain.servicetype.entity.SlotConfig;
import com.homeservice.domain.servicetype.repository.SlotConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotConfigService {

	private final SlotConfigRepository slotConfigRepo;

	// ── Get all slot configs for a city ───────────────
	@Transactional(readOnly = true)
	public List<SlotConfigResponse> getByCity(Long cityId) {

		return slotConfigRepo.findActiveConfigsWithService(cityId).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	// ── Get slot config for city + service ────────────
	@Transactional(readOnly = true)
	public SlotConfigResponse getByCityAndService(Long cityId, Long serviceTypeId) {

		SlotConfig config = slotConfigRepo.findByCityIdAndServiceTypeId(cityId, serviceTypeId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Slot config not found for " + "city: " + cityId + " service: " + serviceTypeId));

		return toResponse(config);
	}

	// ── Update slot config (admin) ────────────────────
	@Transactional
	public SlotConfigResponse update(Long configId, UpdateSlotConfigRequest req) {

		SlotConfig config = slotConfigRepo.findById(configId)
				.orElseThrow(() -> new ResourceNotFoundException("Slot config not found: " + configId));

		if (req.getStartHour() != null)
			config.setStartHour(req.getStartHour());
		if (req.getEndHour() != null)
			config.setEndHour(req.getEndHour());
		if (req.getSlotIntervalMinutes() != null)
			config.setSlotIntervalMinutes(req.getSlotIntervalMinutes());
		if (req.getAdvanceBookingDays() != null)
			config.setAdvanceBookingDays(req.getAdvanceBookingDays());
		if (req.getIsActive() != null)
			config.setIsActive(req.getIsActive());

		slotConfigRepo.save(config);
		return toResponse(config);
	}

	private SlotConfigResponse toResponse(SlotConfig sc) {

		int totalSlots = ((sc.getEndHour() - sc.getStartHour()) * 60) / sc.getSlotIntervalMinutes();

		return SlotConfigResponse.builder().id(sc.getId()).cityId(sc.getCity().getId()).cityName(sc.getCity().getName())
				.serviceTypeId(sc.getServiceType().getId()).serviceTypeName(sc.getServiceType().getName())
				.startHour(sc.getStartHour()).endHour(sc.getEndHour()).slotIntervalMinutes(sc.getSlotIntervalMinutes())
				.advanceBookingDays(sc.getAdvanceBookingDays()).totalSlotsPerDay(totalSlots).isActive(sc.getIsActive())
				.build();
	}
}
