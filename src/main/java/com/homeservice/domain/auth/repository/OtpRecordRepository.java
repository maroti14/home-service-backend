package com.homeservice.domain.auth.repository;



import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.domain.auth.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpRecordRepository extends JpaRepository<OtpRecord, Long> {

    // get the latest unused OTP for a mobile + purpose
    Optional<OtpRecord> findTopByMobileAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(
            String mobile, OtpPurpose purpose);

    // how many OTPs sent in last N minutes — for rate limiting
    @Query("""
            SELECT COUNT(o) FROM OtpRecord o
            WHERE o.mobile = :mobile
              AND o.purpose = :purpose
              AND o.createdAt >= :since
            """)
    long countRecentOtps(String mobile, OtpPurpose purpose,
                         java.time.LocalDateTime since);

    // mark all previous OTPs for this mobile+purpose as used
    @Modifying
    @Transactional
    @Query("""
            UPDATE OtpRecord o SET o.isUsed = true
            WHERE o.mobile = :mobile
              AND o.purpose = :purpose
              AND o.isUsed = false
            """)
    void invalidatePreviousOtps(String mobile, OtpPurpose purpose);
}
