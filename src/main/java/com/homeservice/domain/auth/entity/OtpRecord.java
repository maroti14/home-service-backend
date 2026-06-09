package com.homeservice.domain.auth.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
	    name = "otp_records",
	    indexes = {
	        @Index(
	            name = "idx_otp_mobile_purpose_used",
	            columnList = "mobile, purpose, isUsed"),
	        @Index(
	            name = "idx_otp_expires",
	            columnList = "expiresAt")
	    }
	)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRecord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 15)
	private String mobile;

	@Column(nullable = false, length = 6)
	private String otpCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OtpPurpose purpose;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isUsed = false;

	@Builder.Default
	@Column(nullable = false)
	private Integer attemptCount = 0;
}