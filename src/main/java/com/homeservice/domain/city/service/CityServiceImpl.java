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
import com.homeservice.domain.city.mapper.CityMapper;
import com.homeservice.domain.city.repository.CityConfigRepository;
import com.homeservice.domain.city.repository.CityRepository;
import com.homeservice.domain.servicetype.entity.ServicePricing;
import com.homeservice.domain.servicetype.entity.ServiceType;
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

import static com.homeservice.common.enums.ServiceKey.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityServiceImpl implements CityService {

	private final CityRepository cityRepo;
	private final CityConfigRepository configRepo;
	private final ServiceTypeRepository serviceTypeRepo;
	private final SlotConfigRepository slotConfigRepo;
	private final ServicePricingRepository pricingRepo;
	private final CityMapper cityMapper;

	@Override
	@Transactional(readOnly = true)
	public List<CityResponse> getAllActiveCities() {
		return cityRepo.findByIsActiveTrue().stream().map(cityMapper::toCityResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CityResponse> getAllCities() {
		return cityRepo.findAll().stream().map(cityMapper::toCityResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CityResponse getCityById(Long cityId) {
		return cityMapper.toCityResponse(findCityEntityById(cityId));
	}

	@Override
	@Transactional
	public CityResponse createCity(CreateCityRequest req) {

		if (cityRepo.existsByName(req.getName())) {
			throw new ResourceAlreadyExistsException("City already exists: " + req.getName());
		}
		if (cityRepo.existsByCityCode(req.getCityCode())) {
			throw new ResourceAlreadyExistsException("City code already in use: " + req.getCityCode());
		}

		City city = City.builder().name(req.getName()).state(req.getState()).cityCode(req.getCityCode().toUpperCase())
				.isActive(true).build();

		cityRepo.save(city);
		seedDefaultsForCity(city);

		log.info("City created | name={} code={}", city.getName(), city.getCityCode());

		return cityMapper.toCityResponse(city);
	}

	@Override
	@Transactional
	public CityResponse updateCity(Long cityId, UpdateCityRequest req) {

		City city = findCityEntityById(cityId);

		if (req.getName() != null)
			city.setName(req.getName());
		if (req.getState() != null)
			city.setState(req.getState());
		if (req.getIsActive() != null)
			city.setIsActive(req.getIsActive());

		cityRepo.save(city);
		return cityMapper.toCityResponse(city);
	}

	@Override
	@Transactional(readOnly = true)
	public CityConfigResponse getCityConfig(Long cityId) {
		CityConfig config = configRepo.findByCityId(cityId)
				.orElseThrow(() -> new ResourceNotFoundException("City config not found: " + cityId));
		return cityMapper.toCityConfigResponse(config);
	}

	@Override
	@Transactional
	public CityConfigResponse updateCityConfig(Long cityId, UpdateCityConfigRequest req) {

		CityConfig config = configRepo.findByCityId(cityId)
				.orElseThrow(() -> new ResourceNotFoundException("City config not found: " + cityId));

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

		configRepo.save(config);
		return cityMapper.toCityConfigResponse(config);
	}

	@Override
	@Transactional(readOnly = true)
	public City findCityEntityById(Long cityId) {
		return cityRepo.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityId));
	}

	// ── Private seed helpers ──────────────────────────

	private void seedDefaultsForCity(City city) {
		seedCityConfig(city);
		seedSlotConfigsAndPricing(city);
	}

	private void seedCityConfig(City city) {
		CityConfig config = CityConfig.builder().city(city).build();
		configRepo.save(config);
	}

	private void seedSlotConfigsAndPricing(City city) {
		serviceTypeRepo.findAll().forEach(service -> {
			seedSlotConfig(city, service);
			seedPricing(city, service);
		});
		log.info("Defaults seeded for city | " + "name={}", city.getName());
	}

	private void seedSlotConfig(City city, ServiceType service) {
		if (slotConfigRepo.findByCityIdAndServiceTypeId(city.getId(), service.getId()).isPresent())
			return;

		slotConfigRepo.save(SlotConfig.builder().city(city).serviceType(service).startHour(7).endHour(19)
				.slotIntervalMinutes(30).advanceBookingDays(7).isActive(true).build());
	}

	private void seedPricing(City city, ServiceType service) {
		if (pricingRepo.findByCityIdAndServiceTypeId(city.getId(), service.getId()).isPresent())
			return;

		BigDecimal base;
		BigDecimal perExtra;

		switch (service.getServiceKey()) {
		case CLEANING -> {
			base = bd("199");
			perExtra = bd("149");
		}
		case DISHWASHING -> {
			base = bd("99");
			perExtra = bd("79");
		}
		case MAID -> {
			base = bd("249");
			perExtra = bd("199");
		}
		case COOKING -> {
			base = bd("299");
			perExtra = bd("249");
		}
		case BATHROOM_CLEANING -> {
			base = bd("149");
			perExtra = bd("129");
		}
		case KITCHEN_PREP -> {
			base = bd("199");
			perExtra = bd("149");
		}
		case DUSTING_WIPING -> {
			base = bd("99");
			perExtra = bd("79");
		}
		case BIKE_WASH -> {
			base = bd("99");
			perExtra = bd("79");
		}
		default -> {
			base = bd("149");
			perExtra = bd("99");
		}
		}

		ServicePricing.ServicePricingBuilder b = ServicePricing.builder().city(city).serviceType(service)
				.basePrice(base).pricePerExtraSlot(perExtra).platformFeePercentage(22.0).isActive(true);

		if (service.getServiceKey() == BIKE_WASH) {
			b.engineCleaningPrice(bd("79")).chainLubricationPrice(bd("49")).tyrePollishPrice(bd("39"));
		}
		if (service.getServiceKey() == COOKING) {
			b.groceryAddonPrice(bd("149"));
		}

		pricingRepo.save(b.build());
	}

	private BigDecimal bd(String val) {
		return new BigDecimal(val);
	}
}
