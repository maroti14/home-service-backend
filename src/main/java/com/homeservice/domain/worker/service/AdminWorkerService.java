package com.homeservice.domain.worker.service;

import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;

import java.util.List;

public interface AdminWorkerService {

	List<WorkerProfileResponse> getAllWorkers();

	WorkerProfileResponse getWorker(Long workerId);

	WorkerProfileResponse approveWorker(Long workerId);

	WorkerProfileResponse suspendWorker(Long workerId);

	WorkerDocumentResponse updateDocumentStatus(Long workerId, Long docId, DocumentStatus status,
			String rejectionReason);

	WorkerServiceTagResponse approveServiceTag(Long workerId, Long tagId);
}