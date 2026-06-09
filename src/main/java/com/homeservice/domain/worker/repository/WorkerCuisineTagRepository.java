package com.homeservice.domain.worker.repository;

import com.homeservice.common.enums.CuisineType;
import com.homeservice.domain.worker.entity.WorkerCuisineTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerCuisineTagRepository extends JpaRepository<WorkerCuisineTag, Long> {

	List<WorkerCuisineTag> findByWorkerId(Long workerId);

	boolean existsByWorkerIdAndCuisineType(Long workerId, CuisineType cuisineType);

	void deleteByWorkerIdAndCuisineType(Long workerId, CuisineType cuisineType);
}
