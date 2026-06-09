package com.homeservice.domain.city.service;

import com.homeservice.domain.city.dto.request.CreateCityRequest;
import com.homeservice.domain.city.dto.request.UpdateCityConfigRequest;
import com.homeservice.domain.city.dto.request.UpdateCityRequest;
import com.homeservice.domain.city.dto.response.CityConfigResponse;
import com.homeservice.domain.city.dto.response.CityResponse;
import com.homeservice.domain.city.entity.City;

import java.util.List;

public interface CityService {

	List<CityResponse> getAllActiveCities();

	List<CityResponse> getAllCities();

	CityResponse getCityById(Long cityId);

	CityResponse createCity(CreateCityRequest req);

	CityResponse updateCity(Long cityId, UpdateCityRequest req);

	CityConfigResponse getCityConfig(Long cityId);

	CityConfigResponse updateCityConfig(Long cityId, UpdateCityConfigRequest req);

	// used internally by other services
	City findCityEntityById(Long cityId);
}