package com.homeservice.domain.slot.service;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.common.enums.SlotStatus;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.city.entity.City;
import com.homeservice.domain.city.repository.CityRepository;
import com.homeservice.domain.servicetype.entity.SlotConfig;
import com.homeservice.domain.servicetype.repository.SlotConfigRepository;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import com.homeservice.domain.slot.dto.request.BlockSlotRequest;
import com.homeservice.domain.slot.dto.request.UnblockSlotRequest;
import com.homeservice.domain.slot.dto.response.SlotDto;
import com.homeservice.domain.slot.dto.response.SlotGridResponse;
import com.homeservice.domain.slot.dto.response.SlotHeatmapResponse;
import com.homeservice.domain.slot.entity.BlockedSlot;
import com.homeservice.domain.slot.repository.BlockedSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotServiceImpl implements SlotService {

	private final CityRepository cityRepo;
	private final ServiceTypeRepository serviceTypeRepo;
	private final SlotConfigRepository slotConfigRepo;
	private final BlockedSlotRepository blockedSlotRepo;
	private final SlotCacheService cacheService;
	private final SlotLockService lockService;

	// ── Get slot grid (with cache) ────────────────────

	@Override
	@Transactional(readOnly = true)
	public SlotGridResponse getSlotGrid(Long cityId, ServiceKey serviceKey, LocalDate date) {

		// 1. check cache first
		SlotGridResponse cached = cacheService.get(cityId, serviceKey, date);

		if (cached != null) {
			log.debug("Cache hit | " + "cityId={} service={} date={}", cityId, serviceKey, date);
			return cached;
		}

		// 2. cache miss — build from DB
		log.debug("Cache miss | " + "cityId={} service={} date={}", cityId, serviceKey, date);

		SlotGridResponse grid = buildSlotGrid(cityId, serviceKey, date);

		// 3. put in cache
		cacheService.put(cityId, serviceKey, date, grid);

		return grid;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SlotGridResponse> getSlotGridForDays(Long cityId, ServiceKey serviceKey, LocalDate fromDate, int days) {

		// cap at 7 days
		int safeDays = Math.min(days, 7);

		List<SlotGridResponse> result = new ArrayList<>();

		for (int i = 0; i < safeDays; i++) {
			result.add(getSlotGrid(cityId, serviceKey, fromDate.plusDays(i)));
		}

		return result;
	}

	// ── Admin: Block slot ─────────────────────────────

	@Override
	@Transactional
	public void blockSlot(BlockSlotRequest req, String adminEmail) {

		City city = findCity(req.getCityId());

		// check not already blocked
		if (blockedSlotRepo.existsByCityIdAndServiceKeyAndSlotDateAndSlotStartMinutes(req.getCityId(),
				req.getServiceKey(), req.getSlotDate(), req.getSlotStartMinutes())) {

			throw new ResourceAlreadyExistsException("Slot is already blocked.");
		}

		// check slot is not currently locked
		// by a pending booking
		if (lockService.isLocked(req.getCityId(), req.getServiceKey(), req.getSlotDate(), req.getSlotStartMinutes())) {

			throw new InvalidInputException("Cannot block this slot. " + "A booking is in progress.");
		}

		BlockedSlot blocked = BlockedSlot.builder().city(city).serviceKey(req.getServiceKey())
				.slotDate(req.getSlotDate()).slotStartMinutes(req.getSlotStartMinutes()).reason(req.getReason())
				.blockedBy(adminEmail).build();

		blockedSlotRepo.save(blocked);

		// invalidate cache for this date
		cacheService.invalidate(req.getCityId(), req.getServiceKey(), req.getSlotDate());

		log.info("Slot blocked | " + "cityId={} service={} " + "date={} startMin={}", req.getCityId(),
				req.getServiceKey(), req.getSlotDate(), req.getSlotStartMinutes());
	}

	// ── Admin: Unblock slot ───────────────────────────

	@Override
	@Transactional
	public void unblockSlot(UnblockSlotRequest req) {

		blockedSlotRepo.unblockSlot(req.getCityId(), req.getServiceKey(), req.getSlotDate(), req.getSlotStartMinutes());

		// invalidate cache
		cacheService.invalidate(req.getCityId(), req.getServiceKey(), req.getSlotDate());

		log.info("Slot unblocked | " + "cityId={} service={}", req.getCityId(), req.getServiceKey());
	}

	// ── Admin: Heatmap ────────────────────────────────

	@Override
	@Transactional(readOnly = true)
	public SlotHeatmapResponse getHeatmap(Long cityId, LocalDate fromDate, LocalDate toDate) {

		City city = findCity(cityId);

		// build heatmap data
		// Map<ServiceKey, Map<slotTime, count>>
		Map<ServiceKey, Map<String, Integer>> heatmapData = new HashMap<>();

		String peakSlotTime = "";
		int peakCount = 0;

		for (ServiceKey serviceKey : ServiceKey.values()) {

			Map<String, Integer> slotCounts = new HashMap<>();

			// iterate days in range
			LocalDate current = fromDate;
			while (!current.isAfter(toDate)) {
				SlotGridResponse grid = getSlotGrid(cityId, serviceKey, current);

				for (SlotDto slot : grid.getSlots()) {
					if (slot.getBookingCount() > 0) {
						slotCounts.merge(slot.getStartTime(), slot.getBookingCount(), Integer::sum);
					}
				}
				current = current.plusDays(1);
			}

			if (!slotCounts.isEmpty()) {
				heatmapData.put(serviceKey, slotCounts);

				// find peak
				for (Map.Entry<String, Integer> entry : slotCounts.entrySet()) {
					if (entry.getValue() > peakCount) {
						peakCount = entry.getValue();
						peakSlotTime = entry.getKey();
					}
				}
			}
		}

		return SlotHeatmapResponse.builder().cityId(cityId).cityName(city.getName()).fromDate(fromDate).toDate(toDate)
				.heatmapData(heatmapData).peakSlotTime(peakSlotTime).peakSlotCount(peakCount).build();
	}

	// ── Private helpers ───────────────────────────────

	private SlotGridResponse buildSlotGrid(Long cityId, ServiceKey serviceKey, LocalDate date) {

		City city = findCity(cityId);

		// get slot config for this
		// city + service combination
		SlotConfig config = slotConfigRepo.findByCityIdAndServiceTypeId(cityId, getServiceTypeId(serviceKey))
				.orElse(buildDefaultConfig());

		// get all blocked slots for this date
		Set<Integer> blockedMinutes = blockedSlotRepo.findByCityIdAndServiceKeyAndSlotDate(cityId, serviceKey, date)
				.stream().map(BlockedSlot::getSlotStartMinutes).collect(Collectors.toSet());

		// build slot list
		List<SlotDto> slots = new ArrayList<>();
		int available = 0;
		int booked = 0;
		int blocked = 0;

		int startMinutes = config.getStartHour() * 60;
		int endMinutes = config.getEndHour() * 60;
		int interval = config.getSlotIntervalMinutes();

		for (int startMin = startMinutes; startMin < endMinutes; startMin += interval) {

			int endMin = startMin + interval;

			// determine slot status
			SlotStatus status;
			int bookingCount = 0;

			if (blockedMinutes.contains(startMin)) {
				status = SlotStatus.BLOCKED;
				blocked++;
			} else if (lockService.isLocked(cityId, serviceKey, date, startMin)) {
				// check if locked (payment pending)
				// or permanently booked
				String owner = lockService.getLockOwner(cityId, serviceKey, date, startMin);
				if (owner != null && owner.startsWith("CONFIRMED:")) {
					status = SlotStatus.BOOKED;
					bookingCount = 1;
					booked++;
				} else {
					status = SlotStatus.LOCKED;
					booked++;
				}
			} else {
				status = SlotStatus.AVAILABLE;
				available++;
			}

			slots.add(SlotDto.builder().date(date).startTime(minutesToTime(startMin)).endTime(minutesToTime(endMin))
					.startMinutes(startMin).status(status).bookingCount(bookingCount).build());
		}

		String serviceName = serviceTypeRepo.findByServiceKey(serviceKey).map(st -> st.getName())
				.orElse(serviceKey.name());

		return SlotGridResponse.builder().cityId(cityId).cityName(city.getName()).serviceKey(serviceKey)
				.serviceName(serviceName).date(date).slots(slots).availableCount(available).bookedCount(booked)
				.blockedCount(blocked).build();
	}

	// convert minutes to HH:mm string
	private String minutesToTime(int minutes) {
		int hours = minutes / 60;
		int mins = minutes % 60;
		return String.format("%02d:%02d", hours, mins);
	}

	// get service type ID by service key
	private Long getServiceTypeId(ServiceKey serviceKey) {
		return serviceTypeRepo.findByServiceKey(serviceKey).map(st -> st.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceKey));
	}

	// default config if none found for city
	private SlotConfig buildDefaultConfig() {
		SlotConfig config = new SlotConfig();
		config.setStartHour(7);
		config.setEndHour(19);
		config.setSlotIntervalMinutes(30);
		return config;
	}

	private City findCity(Long cityId) {
		return cityRepo.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityId));
	}
}
