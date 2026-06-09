package com.homeservice.domain.worker.repository;

import com.homeservice.common.enums.DocumentType;
import com.homeservice.domain.worker.entity.WorkerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerDocumentRepository extends JpaRepository<WorkerDocument, Long> {

	List<WorkerDocument> findByWorkerId(Long workerId);

	Optional<WorkerDocument> findByWorkerIdAndDocumentType(Long workerId, DocumentType documentType);

	Optional<WorkerDocument> findByIdAndWorkerId(Long id, Long workerId);
}
