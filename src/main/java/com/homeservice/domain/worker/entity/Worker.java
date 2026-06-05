package com.homeservice.domain.worker.entity;

import com.homeservice.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "workers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "user_id")
public class Worker extends User {

	private String profilePhotoUrl;

	private String city;

	// whether admin has approved this worker
	@Builder.Default
	private Boolean isApproved = false;

	// worker's average rating
	@Builder.Default
	private Double rating = 0.0;

	@Builder.Default
	private Integer totalJobsCompleted = 0;
}
