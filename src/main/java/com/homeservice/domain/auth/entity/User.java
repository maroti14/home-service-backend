package com.homeservice.domain.auth.entity;

import com.homeservice.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users", indexes = { @Index(name = "idx_user_email", columnList = "email"),
		@Index(name = "idx_user_mobile", columnList = "mobile") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(unique = true, length = 150)
	private String email;

	@Column(nullable = false, unique = true, length = 15)
	private String mobile;

	@Column(nullable = false)
	private String password;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isMobileVerified = false;
}