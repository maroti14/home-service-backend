package com.homeservice.domain.servicetype.entity;

import com.homeservice.domain.city.entity.City;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "slot_configs", uniqueConstraints = @UniqueConstraint(columnNames = { "city_id", "service_type_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_type_id", nullable = false)
	private ServiceType serviceType;

	// slots start at 7 AM = 7
	@Builder.Default
	@Column(nullable = false)
	private Integer startHour = 7;

	// slots end at 7 PM = 19
	@Builder.Default
	@Column(nullable = false)
	private Integer endHour = 19;

	// each slot = 30 minutes
	@Builder.Default
	@Column(nullable = false)
	private Integer slotIntervalMinutes = 30;

	// how many days in advance
	@Builder.Default
	@Column(nullable = false)
	private Integer advanceBookingDays = 7;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
