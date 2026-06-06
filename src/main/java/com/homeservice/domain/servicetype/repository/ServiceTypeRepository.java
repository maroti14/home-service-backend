package com.homeservice.domain.servicetype.repository;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.servicetype.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

	Optional<ServiceType> findByServiceKey(ServiceKey serviceKey);

	List<ServiceType> findByIsActiveTrueOrderByDisplayOrderAsc();

	boolean existsByServiceKey(ServiceKey serviceKey);
}
