package com.homeservice.domain.worker.service;

import com.homeservice.common.enums.DocumentType;
import com.homeservice.domain.worker.dto.response.WorkerAvailabilityResponse;
import com.homeservice.domain.worker.dto.response.WorkerCuisineTagResponse;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.dto.request.SaveAvailabilityRequest;
import com.homeservice.domain.worker.dto.request.SaveCuisineTagRequest;
import com.homeservice.domain.worker.dto.request.SaveServiceTagRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkerDocumentService {

	// ── Documents ─────────────────────────────────────
	WorkerDocumentResponse uploadDocument(Long workerId, DocumentType documentType, MultipartFile file);

	List<WorkerDocumentResponse> getDocuments(Long workerId);

	// ── Service Tags ──────────────────────────────────
	WorkerServiceTagResponse addServiceTag(Long workerId, SaveServiceTagRequest req);

	void removeServiceTag(Long workerId, Long tagId);

	List<WorkerServiceTagResponse> getServiceTags(Long workerId);

	// ── Cuisine Tags ──────────────────────────────────
	WorkerCuisineTagResponse addCuisineTag(Long workerId, SaveCuisineTagRequest req);

	void removeCuisineTag(Long workerId, Long tagId);

	List<WorkerCuisineTagResponse> getCuisineTags(Long workerId);

	// ── Availability ──────────────────────────────────
	List<WorkerAvailabilityResponse> saveAvailability(Long workerId, SaveAvailabilityRequest req);

	List<WorkerAvailabilityResponse> getAvailability(Long workerId);
}
