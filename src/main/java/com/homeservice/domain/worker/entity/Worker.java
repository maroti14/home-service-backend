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

	@Column(length = 100)
	private String city;

	private String profilePhotoUrl;

	// admin must approve before
	// worker can receive jobs
	@Builder.Default
	@Column(nullable = false)
	private Boolean isApproved = false;

	// worker toggles this to receive jobs
	@Builder.Default
	@Column(nullable = false)
	private Boolean isOnline = false;

	// rolling average updated after each job
	@Builder.Default
	@Column(nullable = false)
	private Double rating = 0.0;

	@Builder.Default
	@Column(nullable = false)
	private Integer totalRatings = 0;

	@Builder.Default
	@Column(nullable = false)
	private Integer totalJobsCompleted = 0;

	// drops when worker declines jobs
	@Builder.Default
	@Column(nullable = false)
	private Double acceptanceRate = 100.0;

	// years of experience
	@Builder.Default
	@Column(nullable = false)
	private Integer experienceYears = 0;

	// short bio shown on profile
	@Column(length = 500)
	private String bio;
}