package com.homeservice.domain.servicetype.repository;

import com.homeservice.domain.servicetype.entity.ServicePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServicePricingRepository extends JpaRepository<ServicePricing, Long> {

	Optional<ServicePricing> findByCityIdAndServiceTypeId(Long cityId, Long serviceTypeId);

	List<ServicePricing> findByCityIdAndIsActiveTrue(Long cityId);

	@Query("""
			SELECT sp FROM ServicePricing sp
			JOIN FETCH sp.serviceType
			WHERE sp.city.id = :cityId
			  AND sp.isActive = true
			""")
	List<ServicePricing> findActivePricingWithService(Long cityId);
}
