package com.homeservice.domain.city.mapper;

import com.homeservice.domain.city.dto.response.CityConfigResponse;
import com.homeservice.domain.city.dto.response.CityResponse;
import com.homeservice.domain.city.entity.City;
import com.homeservice.domain.city.entity.CityConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CityMapper {

	CityResponse toCityResponse(City city);

	@Mapping(source = "city.id", target = "cityId")
	@Mapping(source = "city.name", target = "cityName")
	CityConfigResponse toCityConfigResponse(CityConfig config);
}
