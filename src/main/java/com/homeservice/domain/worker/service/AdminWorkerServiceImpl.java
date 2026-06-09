package com.homeservice.domain.worker.service;

import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.entity.WorkerDocument;
import com.homeservice.domain.worker.entity.WorkerServiceTag;
import com.homeservice.domain.worker.mapper.WorkerMapper;
import com.homeservice.domain.worker.repository.WorkerDocumentRepository;
import com.homeservice.domain.worker.repository.WorkerRepository;
import com.homeservice.domain.worker.repository.WorkerServiceTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminWorkerServiceImpl implements AdminWorkerService {

	private final WorkerRepository workerRepo;
	private final WorkerDocumentRepository docRepo;
	private final WorkerServiceTagRepository serviceTagRepo;
	private final WorkerMapper workerMapper;

	@Override
	@Transactional(readOnly = true)
	public List<WorkerProfileResponse> getAllWorkers() {
		return workerRepo.findAll().stream().map(workerMapper::toProfileResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public WorkerProfileResponse getWorker(Long workerId) {
		return workerMapper.toProfileResponse(findWorker(workerId));
	}

	@Override
	@Transactional
	public WorkerProfileResponse approveWorker(Long workerId) {

		Worker worker = findWorker(workerId);
		worker.setIsApproved(true);
		worker.setIsActive(true);
		workerRepo.save(worker);

		log.info("Worker approved | workerId={}", workerId);

		return workerMapper.toProfileResponse(worker);
	}

	@Override
	@Transactional
	public WorkerProfileResponse suspendWorker(Long workerId) {

		Worker worker = findWorker(workerId);
		worker.setIsApproved(false);
		worker.setIsOnline(false);
		worker.setIsActive(false);
		workerRepo.save(worker);

		log.info("Worker suspended | workerId={}", workerId);

		return workerMapper.toProfileResponse(worker);
	}

	@Override
	@Transactional
	public WorkerDocumentResponse updateDocumentStatus(Long workerId, Long docId, DocumentStatus status,
			String rejectionReason) {

		WorkerDocument doc = docRepo.findByIdAndWorkerId(docId, workerId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found: " + docId));

		doc.setStatus(status);

		if (status == DocumentStatus.REJECTED && rejectionReason != null) {
			doc.setRejectionReason(rejectionReason);
		} else {
			doc.setRejectionReason(null);
		}

		docRepo.save(doc);

		log.info("Document status updated | " + "docId={} status={}", docId, status);

		return workerMapper.toDocumentResponse(doc);
	}

	@Override
	@Transactional
	public WorkerServiceTagResponse approveServiceTag(Long workerId, Long tagId) {

		WorkerServiceTag tag = serviceTagRepo.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Service tag not found: " + tagId));

		if (!tag.getWorker().getId().equals(workerId)) {
			throw new ResourceNotFoundException("Service tag not found: " + tagId);
		}

		tag.setIsApproved(true);
		serviceTagRepo.save(tag);

		log.info("Service tag approved | " + "tagId={}", tagId);

		return workerMapper.toServiceTagResponse(tag);
	}

	private Worker findWorker(Long workerId) {
		return workerRepo.findById(workerId)
				.orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + workerId));
	}
}
