package com.homeservice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.config.AppProperties;
import com.homeservice.domain.auth.dto.response.TokenResponse;
import com.homeservice.domain.auth.entity.RefreshToken;
import com.homeservice.domain.auth.entity.User;
import com.homeservice.domain.auth.repository.RefreshTokenRepository;
import com.homeservice.security.JwtUtils;
import com.homeservice.security.UserDetailsImpl;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository repo;
	private final JwtUtils jwtUtils;
	private final AppProperties props;

	private static final int MAX_SESSIONS = 5;

	@Override
	@Transactional
	public String create(User user, String deviceInfo, String ipAddress) {

		long active = repo.countActiveByUserId(user.getId(), LocalDateTime.now());

		if (active >= MAX_SESSIONS) {
			repo.revokeAllByUserId(user.getId());
			log.info("Max sessions reached, " + "revoked all | userId={}", user.getId());
		}

		String value = jwtUtils.generateRefreshTokenValue();

		repo.save(RefreshToken.builder().user(user).token(value)
				.expiresAt(LocalDateTime.now().plusSeconds(props.getJwt().getRefreshExpirationMs() / 1000))
				.isRevoked(false).deviceInfo(deviceInfo).ipAddress(ipAddress).build());

		log.info("Refresh token created | " + "userId={}", user.getId());

		return value;
	}

	@Override
	@Transactional
	public TokenResponse refresh(String tokenValue) {

		RefreshToken token = repo.findByTokenAndIsRevokedFalse(tokenValue).orElseThrow(
				() -> new InvalidInputException("Invalid or expired " + "refresh token. " + "Please login again."));

		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			token.setIsRevoked(true);
			repo.save(token);
			throw new InvalidInputException("Refresh token expired. " + "Please login again.");
		}

		UserDetailsImpl details = new UserDetailsImpl(token.getUser());

		String newAccess = jwtUtils.generateAccessToken(details);

		log.info("Access token refreshed | " + "userId={}", token.getUser().getId());

		return TokenResponse.builder().accessToken(newAccess).refreshToken(tokenValue).tokenType("Bearer")
				.expiresIn(props.getJwt().getExpirationMs() / 1000).build();
	}

	@Override
	@Transactional
	public void revoke(String tokenValue) {
		repo.revokeByToken(tokenValue);
		log.info("Token revoked");
	}

	@Override
	@Transactional
	public void revokeAll(Long userId) {
		repo.revokeAllByUserId(userId);
		log.info("All tokens revoked | " + "userId={}", userId);
	}

	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void cleanup() {
		repo.deleteExpiredAndRevoked(LocalDateTime.now());
		log.info("Expired tokens cleaned up");
	}
}