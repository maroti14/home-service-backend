package com.homeservice.domain.worker.repository;

import com.homeservice.domain.worker.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

	Optional<Worker> findByEmail(String email);

	Optional<Worker> findByMobile(String mobile);

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	// find approved + active workers
	// used by dispatch engine later
	List<Worker> findByIsApprovedTrueAndIsActiveTrueAndIsOnlineTrue();

	// find workers in a city
	@Query("""
			SELECT w FROM Worker w
			WHERE w.city = :city
			AND w.isApproved = true
			AND w.isActive = true
			""")
	List<Worker> findActiveByCity(String city);
}
