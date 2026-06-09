package com.homeservice.domain.servicetype.mapper;

import com.homeservice.domain.servicetype.dto.response.ServicePricingResponse;
import com.homeservice.domain.servicetype.dto.response.ServiceTypeResponse;
import com.homeservice.domain.servicetype.entity.ServicePricing;
import com.homeservice.domain.servicetype.entity.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceTypeMapper {

	ServiceTypeResponse toServiceTypeResponse(ServiceType serviceType);

	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "city.name", target = "cityName")
	@Mapping(source = "serviceType.id", target = "serviceTypeId")
	@Mapping(source = "serviceType.name", target = "serviceTypeName")
	ServicePricingResponse toServicePricingResponse(ServicePricing pricing);
}
