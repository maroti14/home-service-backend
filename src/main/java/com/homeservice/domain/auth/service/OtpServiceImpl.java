package com.homeservice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.OtpExpiredException;
import com.homeservice.common.exception.OtpInvalidException;
import com.homeservice.config.AppProperties;
import com.homeservice.domain.auth.entity.OtpRecord;
import com.homeservice.domain.auth.repository.OtpRecordRepository;
import com.homeservice.infrastructure.sms.SmsService;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

	private final OtpRecordRepository otpRepo;
	private final SmsService smsService;
	private final AppProperties props;

	@Override
	@Transactional
	public void generateAndSend(String mobile, OtpPurpose purpose) {

		long recent = otpRepo.countRecentOtps(mobile, purpose, LocalDateTime.now().minusHours(1));

		if (recent >= props.getOtp().getMaxPerHour()) {
			throw new InvalidInputException("Too many OTP requests. " + "Please wait before requesting again.");
		}

		otpRepo.invalidatePreviousOtps(mobile, purpose);

		String code = String.valueOf(100000 + new SecureRandom().nextInt(900000));

		otpRepo.save(OtpRecord.builder().mobile(mobile).otpCode(code).purpose(purpose)
				.expiresAt(LocalDateTime.now().plusMinutes(props.getOtp().getExpiryMinutes())).isUsed(false)
				.attemptCount(0).build());

		smsService.sendOtp(mobile, code);

		log.info("OTP generated | mobile={} " + "purpose={}", mobile, purpose);
	}

	@Override
	@Transactional
	public void verifyOtp(String mobile, String code, OtpPurpose purpose) {

		int maxAttempts = props.getOtp().getMaxAttempts();

		OtpRecord record = otpRepo.findTopByMobileAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(mobile, purpose)
				.orElseThrow(() -> new OtpInvalidException("No active OTP found. " + "Please request a new OTP."));

		if (record.getAttemptCount() >= maxAttempts) {
			record.setIsUsed(true);
			otpRepo.save(record);
			throw new OtpInvalidException("Too many wrong attempts. " + "Please request a new OTP.");
		}

		if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
			record.setIsUsed(true);
			otpRepo.save(record);
			throw new OtpExpiredException("OTP expired. " + "Please request a new one.");
		}

		if (!record.getOtpCode().equals(code)) {
			record.setAttemptCount(record.getAttemptCount() + 1);
			otpRepo.save(record);
			int left = maxAttempts - record.getAttemptCount();
			throw new OtpInvalidException("Invalid OTP. " + left + " attempts remaining.");
		}

		record.setIsUsed(true);
		otpRepo.save(record);

		log.info("OTP verified | mobile={}", mobile);
	}

	@Override
	public int getExpiryMinutes() {
		return props.getOtp().getExpiryMinutes();
	}
}