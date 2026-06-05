package com.homeservice.domain.auth.service;

import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.OtpExpiredException;
import com.homeservice.common.exception.OtpInvalidException;
import com.homeservice.domain.auth.entity.OtpRecord;
import com.homeservice.domain.auth.repository.OtpRecordRepository;
import com.homeservice.infrastructure.sms.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

	private final OtpRecordRepository otpRepo;
	private final SmsService smsService;

	@Value("${app.otp.expiry-minutes:10}")
	private int otpExpiryMinutes;

	@Value("${app.otp.max-per-hour:3}")
	private int maxOtpPerHour;

	// ── Generate and Send ─────────────────────────────
	@Transactional
	public String generateAndSend(String mobile, OtpPurpose purpose) {

		// rate limit check
		long recentCount = otpRepo.countRecentOtps(mobile, purpose, LocalDateTime.now().minusHours(1));

		if (recentCount >= maxOtpPerHour) {
			throw new InvalidInputException("Too many OTP requests. " + "Please wait before " + "requesting again.");
		}

		// invalidate old OTPs
		otpRepo.invalidatePreviousOtps(mobile, purpose);

		// generate new 6-digit OTP
		String code = generateSixDigitOtp();

		// save to DB
		OtpRecord record = OtpRecord.builder().mobile(mobile).otpCode(code).purpose(purpose)
				.expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes)).isUsed(false).attemptCount(0).build();

		otpRepo.save(record);

		// send SMS via MSG91
		smsService.sendOtp(mobile, code);

		return code;
	}

	// ── Verify OTP ────────────────────────────────────
	@Transactional
	public void verifyOtp(String mobile, String code, OtpPurpose purpose) {

		OtpRecord record = otpRepo.findTopByMobileAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(mobile, purpose)
				.orElseThrow(() -> new OtpInvalidException("No active OTP found. " + "Please request a new OTP."));

		// max 5 wrong attempts
		if (record.getAttemptCount() >= 5) {
			record.setIsUsed(true);
			otpRepo.save(record);
			throw new OtpInvalidException("Too many wrong attempts. " + "Please request a new OTP.");
		}

		// check expiry
		if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
			record.setIsUsed(true);
			otpRepo.save(record);
			throw new OtpExpiredException("OTP has expired. " + "Please request a new one.");
		}

		// check code match
		if (!record.getOtpCode().equals(code)) {
			record.setAttemptCount(record.getAttemptCount() + 1);
			otpRepo.save(record);
			int remaining = 5 - record.getAttemptCount();
			throw new OtpInvalidException("Invalid OTP. " + remaining + " attempts remaining.");
		}

		// success — mark used
		record.setIsUsed(true);
		otpRepo.save(record);
	}

	// ── Helper ────────────────────────────────────────
	private String generateSixDigitOtp() {
		SecureRandom random = new SecureRandom();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public int getExpiryMinutes() {
		return otpExpiryMinutes;
	}
}