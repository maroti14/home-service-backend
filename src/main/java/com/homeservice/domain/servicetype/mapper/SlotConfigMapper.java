package com.homeservice.domain.servicetype.mapper;

import com.homeservice.domain.servicetype.dto.response.SlotConfigResponse;
import com.homeservice.domain.servicetype.entity.SlotConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SlotConfigMapper {

	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "city.name", target = "cityName")
	@Mapping(source = "serviceType.id", target = "serviceTypeId")
	@Mapping(source = "serviceType.name", target = "serviceTypeName")
	@Mapping(expression = "java(calculateTotalSlots(slotConfig))", target = "totalSlotsPerDay")
	SlotConfigResponse toSlotConfigResponse(SlotConfig slotConfig);

	default int calculateTotalSlots(SlotConfig sc) {
		return ((sc.getEndHour() - sc.getStartHour()) * 60) / sc.getSlotIntervalMinutes();
	}
}
