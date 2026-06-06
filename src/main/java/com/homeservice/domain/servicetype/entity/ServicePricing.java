package com.homeservice.domain.servicetype.entity;

import com.homeservice.domain.city.entity.City;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_pricing", uniqueConstraints = @UniqueConstraint(columnNames = { "city_id", "service_type_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePricing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_type_id", nullable = false)
	private ServiceType serviceType;

	// base price for minimum slots
	// e.g. ₹199 for first 30 min
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal basePrice;

	// additional price per extra slot
	// e.g. ₹149 per extra 30 min
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal pricePerExtraSlot;

	// platform fee percentage
	// e.g. 22.0 means 22%
	@Builder.Default
	@Column(nullable = false)
	private Double platformFeePercentage = 22.0;

	// ── Bike Wash Add-on Prices ──────────────────────
	// only used for BIKE_WASH service
	@Builder.Default
	@Column(precision = 10, scale = 2)
	private BigDecimal engineCleaningPrice = BigDecimal.ZERO;

	@Builder.Default
	@Column(precision = 10, scale = 2)
	private BigDecimal chainLubricationPrice = BigDecimal.ZERO;

	@Builder.Default
	@Column(precision = 10, scale = 2)
	private BigDecimal tyrePollishPrice = BigDecimal.ZERO;

	// ── Cooking Add-on Prices ────────────────────────
	// only used for COOKING service
	@Builder.Default
	@Column(precision = 10, scale = 2)
	private BigDecimal groceryAddonPrice = BigDecimal.ZERO;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
