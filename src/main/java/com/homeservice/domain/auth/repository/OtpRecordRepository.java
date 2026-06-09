package com.homeservice.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.domain.auth.entity.OtpRecord;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRecordRepository extends JpaRepository<OtpRecord, Long> {

	Optional<OtpRecord> findTopByMobileAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(String mobile, OtpPurpose purpose);

	@Query("""
			SELECT COUNT(o) FROM OtpRecord o
			WHERE o.mobile = :mobile
			AND o.purpose = :purpose
			AND o.createdAt >= :since
			""")
	long countRecentOtps(String mobile, OtpPurpose purpose, LocalDateTime since);

	@Modifying
	@Transactional
	@Query("""
			UPDATE OtpRecord o
			SET o.isUsed = true
			WHERE o.mobile = :mobile
			AND o.purpose = :purpose
			AND o.isUsed = false
			""")
	void invalidatePreviousOtps(String mobile, OtpPurpose purpose);
}