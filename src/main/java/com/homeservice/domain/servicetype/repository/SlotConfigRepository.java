package com.homeservice.domain.servicetype.repository;

import com.homeservice.domain.servicetype.entity.SlotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SlotConfigRepository extends JpaRepository<SlotConfig, Long> {

	Optional<SlotConfig> findByCityIdAndServiceTypeId(Long cityId, Long serviceTypeId);

	List<SlotConfig> findByCityIdAndIsActiveTrue(Long cityId);

	@Query("""
			SELECT sc FROM SlotConfig sc
			JOIN FETCH sc.serviceType
			WHERE sc.city.id = :cityId
			  AND sc.isActive = true
			""")
	List<SlotConfig> findActiveConfigsWithService(Long cityId);
}
