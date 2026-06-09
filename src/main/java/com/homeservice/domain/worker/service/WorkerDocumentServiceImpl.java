package com.homeservice.domain.worker.service;

import com.homeservice.common.enums.CuisineType;
import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.common.enums.DocumentType;
import com.homeservice.common.enums.ServiceKey;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.servicetype.entity.ServiceType;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import com.homeservice.domain.worker.dto.request.SaveAvailabilityRequest;
import com.homeservice.domain.worker.dto.request.SaveCuisineTagRequest;
import com.homeservice.domain.worker.dto.request.SaveServiceTagRequest;
import com.homeservice.domain.worker.dto.response.WorkerAvailabilityResponse;
import com.homeservice.domain.worker.dto.response.WorkerCuisineTagResponse;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.entity.WorkerAvailability;
import com.homeservice.domain.worker.entity.WorkerCuisineTag;
import com.homeservice.domain.worker.entity.WorkerDocument;
import com.homeservice.domain.worker.entity.WorkerServiceTag;
import com.homeservice.domain.worker.mapper.WorkerMapper;
import com.homeservice.domain.worker.repository.WorkerAvailabilityRepository;
import com.homeservice.domain.worker.repository.WorkerCuisineTagRepository;
import com.homeservice.domain.worker.repository.WorkerDocumentRepository;
import com.homeservice.domain.worker.repository.WorkerServiceTagRepository;
import com.homeservice.infrastructure.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerDocumentServiceImpl implements WorkerDocumentService {

	private final WorkerDocumentRepository docRepo;
	private final WorkerServiceTagRepository serviceTagRepo;
	private final WorkerCuisineTagRepository cuisineTagRepo;
	private final WorkerAvailabilityRepository availabilityRepo;
	private final ServiceTypeRepository serviceTypeRepo;
	private final WorkerProfileServiceImpl workerProfileService;
	private final S3StorageService s3Service;
	private final WorkerMapper workerMapper;

	private static final String DOC_FOLDER = "worker-documents";

	// ── Documents ─────────────────────────────────────

	@Override
	@Transactional
	public WorkerDocumentResponse uploadDocument(Long workerId, DocumentType documentType, MultipartFile file) {

		Worker worker = workerProfileService.findWorker(workerId);

		// if document already exists
		// replace it (re-upload)
		docRepo.findByWorkerIdAndDocumentType(workerId, documentType).ifPresent(existing -> {
			s3Service.deleteFile(existing.getFileUrl());
			docRepo.delete(existing);
		});

		String folder = DOC_FOLDER + "/" + workerId + "/" + documentType.name().toLowerCase();

		String fileUrl = s3Service.uploadFile(file, folder);

		WorkerDocument doc = WorkerDocument.builder().worker(worker).documentType(documentType).fileUrl(fileUrl)
				.status(DocumentStatus.PENDING).originalFileName(file.getOriginalFilename()).build();

		docRepo.save(doc);

		log.info("Document uploaded | " + "workerId={} type={}", workerId, documentType);

		return workerMapper.toDocumentResponse(doc);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkerDocumentResponse> getDocuments(Long workerId) {

		return workerMapper.toDocumentResponseList(docRepo.findByWorkerId(workerId));
	}

	// ── Service Tags ──────────────────────────────────

	@Override
	@Transactional
	public WorkerServiceTagResponse addServiceTag(Long workerId, SaveServiceTagRequest req) {

		Worker worker = workerProfileService.findWorker(workerId);

		ServiceKey key = req.getServiceKey();

		if (serviceTagRepo.existsByWorkerIdAndServiceKey(workerId, key)) {
			throw new ResourceAlreadyExistsException("Service tag already added: " + key.name());
		}

		// find the ServiceType entity
		ServiceType serviceType = serviceTypeRepo.findByServiceKey(key)
				.orElseThrow(() -> new ResourceNotFoundException("Service not found: " + key.name()));

		WorkerServiceTag tag = WorkerServiceTag.builder().worker(worker).serviceKey(key).serviceType(serviceType)
				.isApproved(false).build();

		serviceTagRepo.save(tag);

		log.info("Service tag added | " + "workerId={} key={}", workerId, key);

		return workerMapper.toServiceTagResponse(tag);
	}

	@Override
	@Transactional
	public void removeServiceTag(Long workerId, Long tagId) {

		WorkerServiceTag tag = serviceTagRepo.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagId));

		// make sure this tag belongs
		// to this worker
		if (!tag.getWorker().getId().equals(workerId)) {
			throw new ResourceNotFoundException("Tag not found: " + tagId);
		}

		serviceTagRepo.delete(tag);

		log.info("Service tag removed | " + "workerId={} tagId={}", workerId, tagId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkerServiceTagResponse> getServiceTags(Long workerId) {

		return workerMapper.toServiceTagResponseList(serviceTagRepo.findByWorkerId(workerId));
	}

	// ── Cuisine Tags ──────────────────────────────────

	@Override
	@Transactional
	public WorkerCuisineTagResponse addCuisineTag(Long workerId, SaveCuisineTagRequest req) {

		Worker worker = workerProfileService.findWorker(workerId);

		CuisineType type = req.getCuisineType();

		if (cuisineTagRepo.existsByWorkerIdAndCuisineType(workerId, type)) {
			throw new ResourceAlreadyExistsException("Cuisine tag already added: " + type.name());
		}

		// cuisine tags only valid for workers
		// who have COOKING or KITCHEN_PREP service
		boolean hasCookingService = serviceTagRepo.existsByWorkerIdAndServiceKey(workerId, ServiceKey.COOKING)
				|| serviceTagRepo.existsByWorkerIdAndServiceKey(workerId, ServiceKey.KITCHEN_PREP);

		if (!hasCookingService) {
			throw new InvalidInputException(
					"Cuisine tags can only be added " + "if you have Cooking or " + "Kitchen Prep service tag.");
		}

		WorkerCuisineTag tag = WorkerCuisineTag.builder().worker(worker).cuisineType(type).build();

		cuisineTagRepo.save(tag);

		log.info("Cuisine tag added | " + "workerId={} type={}", workerId, type);

		return workerMapper.toCuisineTagResponse(tag);
	}

	@Override
	@Transactional
	public void removeCuisineTag(Long workerId, Long tagId) {

		WorkerCuisineTag tag = cuisineTagRepo.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Cuisine tag not found: " + tagId));

		if (!tag.getWorker().getId().equals(workerId)) {
			throw new ResourceNotFoundException("Cuisine tag not found: " + tagId);
		}

		cuisineTagRepo.delete(tag);

		log.info("Cuisine tag removed | " + "workerId={} tagId={}", workerId, tagId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkerCuisineTagResponse> getCuisineTags(Long workerId) {

		return workerMapper.toCuisineTagResponseList(cuisineTagRepo.findByWorkerId(workerId));
	}

	// ── Availability ──────────────────────────────────

	@Override
	@Transactional
	public List<WorkerAvailabilityResponse> saveAvailability(Long workerId, SaveAvailabilityRequest req) {

		workerProfileService.findWorker(workerId);

		// validate hours
		for (SaveAvailabilityRequest.AvailabilitySlot slot : req.getSlots()) {
			if (slot.getEndHour() <= slot.getStartHour()) {
				throw new InvalidInputException("End hour must be after " + "start hour for " + slot.getDayOfWeek());
			}
		}

		// delete all existing availability
		// and replace with new ones
		availabilityRepo.deleteByWorkerId(workerId);

		Worker worker = workerProfileService.findWorker(workerId);

		List<WorkerAvailability> saved = req.getSlots().stream()
				.map(slot -> WorkerAvailability.builder().worker(worker).dayOfWeek(slot.getDayOfWeek())
						.startHour(slot.getStartHour()).endHour(slot.getEndHour())
						.isAvailable(slot.getIsAvailable() != null ? slot.getIsAvailable() : true).build())
				.collect(Collectors.toList());

		availabilityRepo.saveAll(saved);

		log.info("Availability saved | " + "workerId={} days={}", workerId, saved.size());

		return workerMapper.toAvailabilityResponseList(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkerAvailabilityResponse> getAvailability(Long workerId) {

		return workerMapper.toAvailabilityResponseList(availabilityRepo.findByWorkerId(workerId));
	}
}
