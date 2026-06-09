package com.homeservice.domain.worker.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "worker_availability", uniqueConstraints = @UniqueConstraint(columnNames = { "worker_id",
		"day_of_week" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerAvailability extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id", nullable = false)
	private Worker worker;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false, length = 15)
	private DayOfWeek dayOfWeek;

	// 0 to 23
	@Column(nullable = false)
	private Integer startHour;

	// 0 to 24
	@Column(nullable = false)
	private Integer endHour;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isAvailable = true;
}
