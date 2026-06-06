package com.homeservice.domain.city.repository;

import com.homeservice.domain.city.entity.CityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityConfigRepository extends JpaRepository<CityConfig, Long> {

	Optional<CityConfig> findByCityId(Long cityId);
}
