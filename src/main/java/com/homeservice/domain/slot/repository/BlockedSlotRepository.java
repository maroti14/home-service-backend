package com.homeservice.domain.slot.repository;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.slot.entity.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {

	List<BlockedSlot> findByCityIdAndServiceKeyAndSlotDate(Long cityId, ServiceKey serviceKey, LocalDate slotDate);

	List<BlockedSlot> findByCityIdAndSlotDate(Long cityId, LocalDate slotDate);

	@Modifying
	@Transactional
	@Query("""
			DELETE FROM BlockedSlot b
			WHERE b.city.id = :cityId
			AND b.serviceKey = :serviceKey
			AND b.slotDate = :slotDate
			AND b.slotStartMinutes = :slotStartMinutes
			""")
	void unblockSlot(Long cityId, ServiceKey serviceKey, LocalDate slotDate, Integer slotStartMinutes);

	boolean existsByCityIdAndServiceKeyAndSlotDateAndSlotStartMinutes(Long cityId, ServiceKey serviceKey,
			LocalDate slotDate, Integer slotStartMinutes);
}
