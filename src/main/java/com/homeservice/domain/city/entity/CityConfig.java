package com.homeservice.domain.city.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "city_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false, unique = true)
	private City city;

	// radius in km to search nearby workers
	@Builder.Default
	@Column(nullable = false)
	private Double geoRadiusKm = 3.0;

	// multiply base price during peak hours
	@Builder.Default
	@Column(nullable = false)
	private Double peakHourMultiplier = 1.2;

	// minimum worker rating to be eligible
	// for dispatch
	@Builder.Default
	@Column(nullable = false)
	private Double minWorkerRating = 3.5;

	// platform commission percentage
	// e.g. 22.0 means 22%
	@Builder.Default
	@Column(nullable = false)
	private Double commissionPercentage = 22.0;

	// how many days in advance
	// customer can book
	@Builder.Default
	@Column(nullable = false)
	private Integer advanceBookingDays = 7;

	// max dispatch retries before
	// escalating to support
	@Builder.Default
	@Column(nullable = false)
	private Integer maxDispatchRetries = 3;

	// seconds worker has to accept
	// a job alert
	@Builder.Default
	@Column(nullable = false)
	private Integer jobAlertTimeoutSeconds = 30;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
