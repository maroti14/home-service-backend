package com.homeservice.domain.servicetype.entity;

import com.homeservice.common.enums.ServiceKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// unique key for this service
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private ServiceKey serviceKey;

	// display name
	// e.g. "Home Cleaning"
	@Column(nullable = false)
	private String name;

	@Column(length = 500)
	private String description;

	// icon identifier for mobile app
	// e.g. "ic_cleaning"
	private String iconName;

	// hex color code
	// e.g. "#F97316"
	@Column(length = 10)
	private String colorCode;

	// background color
	// e.g. "#FFF0E6"
	@Column(length = 10)
	private String bgColorCode;

	// minimum slots required to book
	// e.g. Cleaning = 2 (1 hour minimum)
	@Builder.Default
	@Column(nullable = false)
	private Integer minSlots = 1;

	// maximum slots allowed per booking
	@Builder.Default
	@Column(nullable = false)
	private Integer maxSlots = 6;

	// each slot = 30 minutes
	@Builder.Default
	@Column(nullable = false)
	private Integer slotDurationMinutes = 30;

	// does this service need cuisine tags
	// only true for COOKING and KITCHEN_PREP
	@Builder.Default
	@Column(nullable = false)
	private Boolean requiresCuisineTag = false;

	// does this service need
	// before and after photos
	@Builder.Default
	@Column(nullable = false)
	private Boolean requiresPhotos = true;

	// NEW badge shown on home screen
	@Builder.Default
	@Column(nullable = false)
	private Boolean isNew = false;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;

	// display order on home screen
	@Builder.Default
	@Column(nullable = false)
	private Integer displayOrder = 0;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
