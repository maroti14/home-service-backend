package com.homeservice.domain.city.service;

import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.city.dto.request.CreateCityRequest;
import com.homeservice.domain.city.dto.request.UpdateCityConfigRequest;
import com.homeservice.domain.city.dto.request.UpdateCityRequest;
import com.homeservice.domain.city.dto.response.CityConfigResponse;
import com.homeservice.domain.city.dto.response.CityResponse;
import com.homeservice.domain.city.entity.City;
import com.homeservice.domain.city.entity.CityConfig;
import com.homeservice.domain.city.repository.CityConfigRepository;
import com.homeservice.domain.city.repository.CityRepository;
import com.homeservice.domain.servicetype.entity.ServiceType;
import com.homeservice.domain.servicetype.entity.ServicePricing;
import com.homeservice.domain.servicetype.entity.SlotConfig;
import com.homeservice.domain.servicetype.repository.ServicePricingRepository;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import com.homeservice.domain.servicetype.repository.SlotConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

	private final CityRepository cityRepo;
	private final CityConfigRepository cityConfigRepo;
	private final ServiceTypeRepository serviceTypeRepo;
	private final SlotConfigRepository slotConfigRepo;
	private final ServicePricingRepository pricingRepo;

	// ── Get all active cities ─────────────────────────
	@Transactional(readOnly = true)
	public List<CityResponse> getAllActiveCities() {
		return cityRepo.findByIsActiveTrue().stream().map(this::toCityResponse).collect(Collectors.toList());
	}

	// ── Get all cities (admin) ────────────────────────
	@Transactional(readOnly = true)
	public List<CityResponse> getAllCities() {
		return cityRepo.findAll().stream().map(this::toCityResponse).collect(Collectors.toList());
	}

	// ── Get city by id ────────────────────────────────
	@Transactional(readOnly = true)
	public CityResponse getCityById(Long cityId) {
		return toCityResponse(findCityById(cityId));
	}

	// ── Create city ───────────────────────────────────
	@Transactional
	public CityResponse createCity(CreateCityRequest req) {

		if (cityRepo.existsByName(req.getName())) {
			throw new ResourceAlreadyExistsException("City already exists: " + req.getName());
		}

		if (cityRepo.existsByCityCode(req.getCityCode())) {
			throw new ResourceAlreadyExistsException("City code already exists: " + req.getCityCode());
		}

		City city = City.builder().name(req.getName()).state(req.getState()).cityCode(req.getCityCode().toUpperCase())
				.isActive(true).build();

		cityRepo.save(city);

		// auto-create default CityConfig
		createDefaultCityConfig(city);

		// auto-create SlotConfig and Pricing
		// for all 8 services
		createDefaultSlotConfigsAndPricing(city);

		log.info("City created: {} [{}]", city.getName(), city.getCityCode());

		return toCityResponse(city);
	}

	// ── Update city ───────────────────────────────────
	@Transactional
	public CityResponse updateCity(Long cityId, UpdateCityRequest req) {

		City city = findCityById(cityId);

		if (req.getName() != null)
			city.setName(req.getName());
		if (req.getState() != null)
			city.setState(req.getState());
		if (req.getIsActive() != null)
			city.setIsActive(req.getIsActive());

		cityRepo.save(city);
		return toCityResponse(city);
	}

	// ── Get city config ───────────────────────────────
	@Transactional(readOnly = true)
	public CityConfigResponse getCityConfig(Long cityId) {

		CityConfig config = cityConfigRepo.findByCityId(cityId)
				.orElseThrow(() -> new ResourceNotFoundException("City config not found " + "for city: " + cityId));

		return toCityConfigResponse(config);
	}

	// ── Update city config ────────────────────────────
	@Transactional
	public CityConfigResponse updateCityConfig(Long cityId, UpdateCityConfigRequest req) {

		CityConfig config = cityConfigRepo.findByCityId(cityId)
				.orElseThrow(() -> new ResourceNotFoundException("City config not found"));

		if (req.getGeoRadiusKm() != null)
			config.setGeoRadiusKm(req.getGeoRadiusKm());
		if (req.getPeakHourMultiplier() != null)
			config.setPeakHourMultiplier(req.getPeakHourMultiplier());
		if (req.getMinWorkerRating() != null)
			config.setMinWorkerRating(req.getMinWorkerRating());
		if (req.getCommissionPercentage() != null)
			config.setCommissionPercentage(req.getCommissionPercentage());
		if (req.getAdvanceBookingDays() != null)
			config.setAdvanceBookingDays(req.getAdvanceBookingDays());
		if (req.getMaxDispatchRetries() != null)
			config.setMaxDispatchRetries(req.getMaxDispatchRetries());
		if (req.getJobAlertTimeoutSeconds() != null)
			config.setJobAlertTimeoutSeconds(req.getJobAlertTimeoutSeconds());

		cityConfigRepo.save(config);
		return toCityConfigResponse(config);
	}

	// ── Private helpers ───────────────────────────────

	private void createDefaultCityConfig(City city) {
		CityConfig config = CityConfig.builder().city(city).geoRadiusKm(3.0).peakHourMultiplier(1.2)
				.minWorkerRating(3.5).commissionPercentage(22.0).advanceBookingDays(7).maxDispatchRetries(3)
				.jobAlertTimeoutSeconds(30).build();
		cityConfigRepo.save(config);
	}

	private void createDefaultSlotConfigsAndPricing(City city) {

		List<ServiceType> services = serviceTypeRepo.findAll();

		for (ServiceType service : services) {
			// SlotConfig
			if (slotConfigRepo.findByCityIdAndServiceTypeId(city.getId(), service.getId()).isEmpty()) {

				SlotConfig slotConfig = SlotConfig.builder().city(city).serviceType(service).startHour(7).endHour(19)
						.slotIntervalMinutes(30).advanceBookingDays(7).isActive(true).build();
				slotConfigRepo.save(slotConfig);
			}

			// ServicePricing
			if (pricingRepo.findByCityIdAndServiceTypeId(city.getId(), service.getId()).isEmpty()) {

				ServicePricing pricing = buildDefaultPricing(city, service);
				pricingRepo.save(pricing);
			}
		}

		log.info("Default slot configs and " + "pricing created for city: {}", city.getName());
	}

	private ServicePricing buildDefaultPricing(City city, ServiceType service) {

		// default prices per service type
		BigDecimal base;
		BigDecimal perExtra;

		switch (service.getServiceKey()) {
		case CLEANING -> {
			base = new BigDecimal("199");
			perExtra = new BigDecimal("149");
		}
		case DISHWASHING -> {
			base = new BigDecimal("99");
			perExtra = new BigDecimal("79");
		}
		case MAID -> {
			base = new BigDecimal("249");
			perExtra = new BigDecimal("199");
		}
		case COOKING -> {
			base = new BigDecimal("299");
			perExtra = new BigDecimal("249");
		}
		case BATHROOM_CLEANING -> {
			base = new BigDecimal("149");
			perExtra = new BigDecimal("129");
		}
		case KITCHEN_PREP -> {
			base = new BigDecimal("199");
			perExtra = new BigDecimal("149");
		}
		case DUSTING_WIPING -> {
			base = new BigDecimal("99");
			perExtra = new BigDecimal("79");
		}
		case BIKE_WASH -> {
			base = new BigDecimal("99");
			perExtra = new BigDecimal("79");
		}
		default -> {
			base = new BigDecimal("149");
			perExtra = new BigDecimal("99");
		}
		}

		ServicePricing.ServicePricingBuilder builder = ServicePricing.builder().city(city).serviceType(service)
				.basePrice(base).pricePerExtraSlot(perExtra).platformFeePercentage(22.0).isActive(true);

		// bike wash add-ons
		if (service.getServiceKey() == com.homeservice.common.enums.ServiceKey.BIKE_WASH) {
			builder.engineCleaningPrice(new BigDecimal("79")).chainLubricationPrice(new BigDecimal("49"))
					.tyrePollishPrice(new BigDecimal("39"));
		}

		// cooking add-on
		if (service.getServiceKey() == com.homeservice.common.enums.ServiceKey.COOKING) {
			builder.groceryAddonPrice(new BigDecimal("149"));
		}

		return builder.build();
	}

	public City findCityById(Long cityId) {
		return cityRepo.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityId));
	}

	private CityResponse toCityResponse(City city) {
		return CityResponse.builder().id(city.getId()).name(city.getName()).state(city.getState())
				.cityCode(city.getCityCode()).isActive(city.getIsActive()).createdAt(city.getCreatedAt()).build();
	}

	private CityConfigResponse toCityConfigResponse(CityConfig config) {
		return CityConfigResponse.builder().id(config.getId()).cityId(config.getCity().getId())
				.cityName(config.getCity().getName()).geoRadiusKm(config.getGeoRadiusKm())
				.peakHourMultiplier(config.getPeakHourMultiplier()).minWorkerRating(config.getMinWorkerRating())
				.commissionPercentage(config.getCommissionPercentage())
				.advanceBookingDays(config.getAdvanceBookingDays()).maxDispatchRetries(config.getMaxDispatchRetries())
				.jobAlertTimeoutSeconds(config.getJobAlertTimeoutSeconds()).build();
	}
}
