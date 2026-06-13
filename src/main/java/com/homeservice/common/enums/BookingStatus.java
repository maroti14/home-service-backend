package com.homeservice.common.enums;

public enum BookingStatus {

	// booking created, payment pending
	PENDING,

	// payment successful, finding worker
	CONFIRMED,

	// worker assigned
	WORKER_ASSIGNED,

	// worker accepted the job
	WORKER_ACCEPTED,

	// worker is travelling to customer
	EN_ROUTE,

	// worker arrived at customer address
	WORKER_ARRIVED,

	// job is being performed
	IN_PROGRESS,

	// worker marked job complete
	// waiting for customer confirmation
	COMPLETED_BY_WORKER,

	// customer confirmed or auto-confirmed
	COMPLETED,

	// booking cancelled
	CANCELLED
}