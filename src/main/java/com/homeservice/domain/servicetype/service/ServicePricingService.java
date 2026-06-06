package com.homeservice.domain.servicetype.service;

import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.servicetype.dto.request.UpdateServicePricingRequest;
import com.homeservice.domain.servicetype.dto.response.ServicePricingResponse;
import com.homeservice.domain.servicetype.entity.ServicePricing;
import com.homeservice.domain.servicetype.repository.ServicePricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicePricingService {

	private final ServicePricingRepository pricingRepo;

	// ── Get all pricing for a city ────────────────────
	@Transactional(readOnly = true)
	public List<ServicePricingResponse> getByCity(Long cityId) {

		return pricingRepo.findActivePricingWithService(cityId).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	// ── Get pricing for city + service ────────────────
	@Transactional(readOnly = true)
	public ServicePricingResponse getByCityAndService(Long cityId, Long serviceTypeId) {

		ServicePricing pricing = pricingRepo.findByCityIdAndServiceTypeId(cityId, serviceTypeId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Pricing not found for " + "city: " + cityId + " service: " + serviceTypeId));

		return toResponse(pricing);
	}

	// ── Update pricing (admin) ────────────────────────
	@Transactional
	public ServicePricingResponse update(Long pricingId, UpdateServicePricingRequest req) {

		ServicePricing pricing = pricingRepo.findById(pricingId)
				.orElseThrow(() -> new ResourceNotFoundException("Pricing not found: " + pricingId));

		if (req.getBasePrice() != null)
			pricing.setBasePrice(req.getBasePrice());
		if (req.getPricePerExtraSlot() != null)
			pricing.setPricePerExtraSlot(req.getPricePerExtraSlot());
		if (req.getPlatformFeePercentage() != null)
			pricing.setPlatformFeePercentage(req.getPlatformFeePercentage());
		if (req.getEngineCleaningPrice() != null)
			pricing.setEngineCleaningPrice(req.getEngineCleaningPrice());
		if (req.getChainLubricationPrice() != null)
			pricing.setChainLubricationPrice(req.getChainLubricationPrice());
		if (req.getTyrePollishPrice() != null)
			pricing.setTyrePollishPrice(req.getTyrePollishPrice());
		if (req.getGroceryAddonPrice() != null)
			pricing.setGroceryAddonPrice(req.getGroceryAddonPrice());
		if (req.getIsActive() != null)
			pricing.setIsActive(req.getIsActive());

		pricingRepo.save(pricing);
		return toResponse(pricing);
	}

	private ServicePricingResponse toResponse(ServicePricing sp) {
		return ServicePricingResponse.builder().id(sp.getId()).cityId(sp.getCity().getId())
				.cityName(sp.getCity().getName()).serviceTypeId(sp.getServiceType().getId())
				.serviceTypeName(sp.getServiceType().getName()).basePrice(sp.getBasePrice())
				.pricePerExtraSlot(sp.getPricePerExtraSlot()).platformFeePercentage(sp.getPlatformFeePercentage())
				.engineCleaningPrice(sp.getEngineCleaningPrice()).chainLubricationPrice(sp.getChainLubricationPrice())
				.tyrePollishPrice(sp.getTyrePollishPrice()).groceryAddonPrice(sp.getGroceryAddonPrice())
				.isActive(sp.getIsActive()).build();
	}
}
