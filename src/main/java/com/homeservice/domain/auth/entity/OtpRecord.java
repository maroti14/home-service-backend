package com.homeservice.domain.auth.entity;



import com.homeservice.common.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // mobile number the OTP was sent to
    @Column(nullable = false, length = 15)
    private String mobile;

    @Column(nullable = false, length = 6)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    private Boolean isUsed = false;

    // how many times user attempted wrong OTP
    @Builder.Default
    private Integer attemptCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
