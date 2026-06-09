package com.homeservice.common.enums;

public enum SlotStatus {

	// slot is free — can be booked
	AVAILABLE,

	// slot is locked for a booking
	// in progress (payment pending)
	LOCKED,

	// slot has a confirmed booking
	BOOKED,

	// admin has blocked this slot
	BLOCKED
}
