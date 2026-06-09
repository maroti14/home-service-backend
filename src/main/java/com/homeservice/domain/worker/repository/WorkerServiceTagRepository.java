package com.homeservice.domain.worker.repository;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.worker.entity.WorkerServiceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerServiceTagRepository extends JpaRepository<WorkerServiceTag, Long> {

	List<WorkerServiceTag> findByWorkerId(Long workerId);

	List<WorkerServiceTag> findByWorkerIdAndIsApprovedTrue(Long workerId);

	Optional<WorkerServiceTag> findByWorkerIdAndServiceKey(Long workerId, ServiceKey serviceKey);

	boolean existsByWorkerIdAndServiceKey(Long workerId, ServiceKey serviceKey);

	void deleteByWorkerIdAndServiceKey(Long workerId, ServiceKey serviceKey);
}
