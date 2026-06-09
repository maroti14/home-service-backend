package com.homeservice.domain.worker.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.servicetype.entity.ServiceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "worker_service_tags", uniqueConstraints = @UniqueConstraint(columnNames = { "worker_id",
		"service_key" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerServiceTag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id", nullable = false)
	private Worker worker;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ServiceKey serviceKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_type_id")
	private ServiceType serviceType;

	// admin must approve each service tag
	@Builder.Default
	@Column(nullable = false)
	private Boolean isApproved = false;
}
