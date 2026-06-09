package com.homeservice.domain.city.entity;

import com.homeservice.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "city_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityConfig extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false, unique = true)
	private City city;

	@Builder.Default
	@Column(nullable = false)
	private Double geoRadiusKm = 3.0;

	@Builder.Default
	@Column(nullable = false)
	private Double peakHourMultiplier = 1.2;

	@Builder.Default
	@Column(nullable = false)
	private Double minWorkerRating = 3.5;

	@Builder.Default
	@Column(nullable = false)
	private Double commissionPercentage = 22.0;

	@Builder.Default
	@Column(nullable = false)
	private Integer advanceBookingDays = 7;

	@Builder.Default
	@Column(nullable = false)
	private Integer maxDispatchRetries = 3;

	@Builder.Default
	@Column(nullable = false)
	private Integer jobAlertTimeoutSeconds = 30;
}