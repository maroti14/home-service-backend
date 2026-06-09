package com.homeservice.domain.slot.service;

import com.homeservice.common.enums.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotLockService {

	private final RedisTemplate<String, Object> redisTemplate;

	// lock TTL — 5 minutes for payment processing
	private static final long LOCK_TTL_SECONDS = 300L;

	// ── Build lock key ────────────────────────────────

	private String buildLockKey(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes) {

		return "slot_lock:" + cityId + ":" + serviceKey.name() + ":" + date.toString() + ":" + slotStartMinutes;
	}

	// ── Acquire lock ──────────────────────────────────
	// Returns true if lock acquired
	// Returns false if already locked

	public boolean acquireLock(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes,
			String bookingReference) {

		String key = buildLockKey(cityId, serviceKey, date, slotStartMinutes);

		// SET key value NX EX 300
		// NX = only set if not exists
		// EX = expire in 300 seconds
		Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, bookingReference, LOCK_TTL_SECONDS,
				TimeUnit.SECONDS);

		boolean result = Boolean.TRUE.equals(acquired);

		if (result) {
			log.info("Slot lock acquired | " + "key={} ref={}", key, bookingReference);
		} else {
			log.warn("Slot already locked | " + "key={}", key);
		}

		return result;
	}

	// ── Release lock ──────────────────────────────────
	// Called on payment failure

	public void releaseLock(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes) {

		String key = buildLockKey(cityId, serviceKey, date, slotStartMinutes);

		redisTemplate.delete(key);

		log.info("Slot lock released | key={}", key);
	}

	// ── Persist lock ──────────────────────────────────
	// Called on payment success
	// Removes TTL so lock stays permanently
	// (until booking is cancelled or completed)

	public void persistLock(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes) {

		String key = buildLockKey(cityId, serviceKey, date, slotStartMinutes);

		// persist = remove the TTL
		redisTemplate.persist(key);

		log.info("Slot lock persisted | key={}", key);
	}

	// ── Check if slot is locked ───────────────────────

	public boolean isLocked(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes) {

		String key = buildLockKey(cityId, serviceKey, date, slotStartMinutes);

		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	// ── Get booking reference from lock ───────────────

	public String getLockOwner(Long cityId, ServiceKey serviceKey, LocalDate date, Integer slotStartMinutes) {

		String key = buildLockKey(cityId, serviceKey, date, slotStartMinutes);

		Object value = redisTemplate.opsForValue().get(key);

		return value != null ? value.toString() : null;
	}
}
