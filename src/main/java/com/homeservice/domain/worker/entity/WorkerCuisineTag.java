package com.homeservice.domain.worker.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.CuisineType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "worker_cuisine_tags", uniqueConstraints = @UniqueConstraint(columnNames = { "worker_id",
		"cuisine_type" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerCuisineTag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id", nullable = false)
	private Worker worker;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private CuisineType cuisineType;
}
