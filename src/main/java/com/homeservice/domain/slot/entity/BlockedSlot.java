package com.homeservice.domain.slot.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.city.entity.City;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "blocked_slots", indexes = {
		@Index(name = "idx_blocked_city_service_date", columnList = "city_id, service_key, slot_date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedSlot extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@Enumerated(EnumType.STRING)
	@Column(name = "service_key", nullable = false, length = 30)
	private ServiceKey serviceKey;

	// the date this slot is blocked
	@Column(nullable = false)
	private LocalDate slotDate;

	// slot start time in minutes from midnight
	// e.g. 7:00 AM = 420, 7:30 AM = 450
	@Column(nullable = false)
	private Integer slotStartMinutes;

	// reason why admin blocked this slot
	@Column(length = 300)
	private String reason;

	// admin who blocked it
	@Column(length = 100)
	private String blockedBy;
}
