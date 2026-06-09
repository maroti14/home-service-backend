package com.homeservice.domain.auth.entity;

import com.homeservice.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens", indexes = { @Index(name = "idx_refresh_token_token", columnList = "token"),
		@Index(name = "idx_refresh_token_user", columnList = "user_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, unique = true, length = 512)
	private String token;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isRevoked = false;

	// device info for multi-device support
	@Column(length = 200)
	private String deviceInfo;

	// IP address of the device
	@Column(length = 50)
	private String ipAddress;
}
