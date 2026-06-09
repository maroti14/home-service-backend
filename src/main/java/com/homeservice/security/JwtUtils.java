package com.homeservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.homeservice.config.AppProperties;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

	private final AppProperties appProperties;

	public String generateAccessToken(UserDetailsImpl principal) {

		Set<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		return Jwts.builder().subject(principal.getUsername()).claim("roles", roles)
				.claim("userId", principal.getUserId()).claim("mobile", principal.getMobile()).claim("type", "ACCESS")
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getExpirationMs()))
				.signWith(secretKey()).compact();
	}

	public String generateRefreshTokenValue() {
		return UUID.randomUUID() + "-" + UUID.randomUUID();
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwts.parser().verifyWith((SecretKey) secretKey()).build().parseSignedClaims(token);
			return true;
		} catch (MalformedJwtException e) {
			log.warn("Invalid JWT | error={}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.warn("Expired JWT | error={}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.warn("Unsupported JWT | error={}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("Empty JWT | error={}", e.getMessage());
		}
		return false;
	}

	public String getUsernameFromToken(String token) {
		return Jwts.parser().verifyWith((SecretKey) secretKey()).build().parseSignedClaims(token).getPayload()
				.getSubject();
	}

	private Key secretKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getJwt().getSecret()));
	}
}