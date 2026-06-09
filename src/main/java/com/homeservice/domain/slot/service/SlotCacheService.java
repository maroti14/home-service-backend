package com.homeservice.domain.slot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.slot.dto.response.SlotGridResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotCacheService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	// cache TTL — 60 seconds
	private static final long CACHE_TTL_SECONDS = 60L;

	// ── Cache key builder ─────────────────────────────

	public String buildCacheKey(Long cityId, ServiceKey serviceKey, LocalDate date) {

		return "slots:" + cityId + ":" + serviceKey.name() + ":" + date.toString();
	}

	// ── Get from cache ────────────────────────────────

	public SlotGridResponse get(Long cityId, ServiceKey serviceKey, LocalDate date) {

		String key = buildCacheKey(cityId, serviceKey, date);

		try {
			Object value = redisTemplate.opsForValue().get(key);
			if (value == null)
				return null;

			return objectMapper.convertValue(value, SlotGridResponse.class);

		} catch (Exception e) {
			log.warn("Cache get failed | " + "key={} error={}", key, e.getMessage());
			return null;
		}
	}

	// ── Put to cache ──────────────────────────────────

	public void put(Long cityId, ServiceKey serviceKey, LocalDate date, SlotGridResponse grid) {

		String key = buildCacheKey(cityId, serviceKey, date);

		try {
			redisTemplate.opsForValue().set(key, grid, CACHE_TTL_SECONDS, TimeUnit.SECONDS);

			log.debug("Cache set | key={}", key);

		} catch (Exception e) {
			log.warn("Cache put failed | " + "key={} error={}", key, e.getMessage());
		}
	}

	// ── Invalidate cache ──────────────────────────────

	public void invalidate(Long cityId, ServiceKey serviceKey, LocalDate date) {

		String key = buildCacheKey(cityId, serviceKey, date);

		try {
			redisTemplate.delete(key);
			log.debug("Cache invalidated | " + "key={}", key);
		} catch (Exception e) {
			log.warn("Cache invalidate failed | " + "key={} error={}", key, e.getMessage());
		}
	}
}
