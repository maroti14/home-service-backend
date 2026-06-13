package com.homeservice.domain.booking.service;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.booking.dto.request.BikeWashDetailRequest;
import com.homeservice.domain.booking.dto.request.CreateBookingRequest;
import com.homeservice.domain.booking.dto.response.PriceBreakdownResponse;
import com.homeservice.domain.servicetype.entity.ServicePricing;
import com.homeservice.domain.servicetype.repository.ServicePricingRepository;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {

	private final ServicePricingRepository pricingRepo;
	private final ServiceTypeRepository serviceTypeRepo;

	@Override
	@Transactional(readOnly = true)
	public PriceBreakdownResponse calculate(CreateBookingRequest req) {

		// get pricing config for this
		// city + service combination
		Long serviceTypeId = serviceTypeRepo.findByServiceKey(req.getServiceKey()).map(st -> st.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + req.getServiceKey()));

		ServicePricing pricing = pricingRepo.findByCityIdAndServiceTypeId(req.getCityId(), serviceTypeId)
				.orElseThrow(() -> new ResourceNotFoundException("Pricing not found for " + "city and service."));

		// base price for first slot
		BigDecimal base = pricing.getBasePrice();

		// extra slots price
		BigDecimal extraSlotsPrice = BigDecimal.ZERO;

		if (req.getTotalSlots() > 1) {
			extraSlotsPrice = pricing.getPricePerExtraSlot().multiply(BigDecimal.valueOf(req.getTotalSlots() - 1));
		}

		// add-ons price (Bike Wash only)
		BigDecimal addOnsPrice = BigDecimal.ZERO;

		if (req.getServiceKey() == ServiceKey.BIKE_WASH && req.getBikeWashDetail() != null) {

			BikeWashDetailRequest bw = req.getBikeWashDetail();

			if (Boolean.TRUE.equals(bw.getEngineCleaning())) {
				addOnsPrice = addOnsPrice.add(pricing.getEngineCleaningPrice());
			}
			if (Boolean.TRUE.equals(bw.getChainLubrication())) {
				addOnsPrice = addOnsPrice.add(pricing.getChainLubricationPrice());
			}
			if (Boolean.TRUE.equals(bw.getTyrePollish())) {
				addOnsPrice = addOnsPrice.add(pricing.getTyrePollishPrice());
			}
		}

		// cooking grocery add-on
		if (req.getServiceKey() == ServiceKey.COOKING && req.getCookingDetail() != null
				&& Boolean.TRUE.equals(req.getCookingDetail().getGroceryAddon())) {
			addOnsPrice = addOnsPrice.add(pricing.getGroceryAddonPrice());
		}

		// subtotal
		BigDecimal subtotal = base.add(extraSlotsPrice).add(addOnsPrice);

		// platform fee
		BigDecimal feePercent = BigDecimal.valueOf(pricing.getPlatformFeePercentage()).divide(BigDecimal.valueOf(100));

		BigDecimal platformFee = subtotal.multiply(feePercent).setScale(2, RoundingMode.HALF_UP);

		// total = subtotal + platform fee
		BigDecimal total = subtotal.add(platformFee);

		// worker earnings = total - platform fee
		BigDecimal workerEarnings = total.subtract(platformFee);

		log.info("Price calculated | " + "service={} total={}", req.getServiceKey(), total);

		return PriceBreakdownResponse.builder().basePrice(base).extraSlotsPrice(extraSlotsPrice)
				.addOnsPrice(addOnsPrice).platformFee(platformFee).totalAmount(total).workerEarnings(workerEarnings)
				.platformFeePercentage(pricing.getPlatformFeePercentage())
				.description(buildDescription(req, base, extraSlotsPrice, addOnsPrice)).build();
	}

	private String buildDescription(CreateBookingRequest req, BigDecimal base, BigDecimal extra, BigDecimal addOns) {

		StringBuilder sb = new StringBuilder();
		sb.append("Base ₹").append(base);

		if (req.getTotalSlots() > 1) {
			sb.append(" + Extra slots ₹").append(extra);
		}
		if (addOns.compareTo(BigDecimal.ZERO) > 0) {
			sb.append(" + Add-ons ₹").append(addOns);
		}

		return sb.toString();
	}
}
