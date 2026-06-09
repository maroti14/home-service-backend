package com.homeservice.domain.worker.repository;

import com.homeservice.common.enums.DayOfWeek;
import com.homeservice.domain.worker.entity.WorkerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerAvailabilityRepository extends JpaRepository<WorkerAvailability, Long> {

	List<WorkerAvailability> findByWorkerId(Long workerId);

	Optional<WorkerAvailability> findByWorkerIdAndDayOfWeek(Long workerId, DayOfWeek dayOfWeek);

	void deleteByWorkerId(Long workerId);
}
