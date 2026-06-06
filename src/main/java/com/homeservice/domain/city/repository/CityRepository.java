package com.homeservice.domain.city.repository;

import com.homeservice.domain.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

	Optional<City> findByCityCode(String cityCode);

	Optional<City> findByName(String name);

	List<City> findByIsActiveTrue();

	boolean existsByCityCode(String cityCode);

	boolean existsByName(String name);
}
