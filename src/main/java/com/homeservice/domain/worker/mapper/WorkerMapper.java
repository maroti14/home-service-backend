package com.homeservice.domain.worker.mapper;

import com.homeservice.domain.worker.dto.response.WorkerAvailabilityResponse;
import com.homeservice.domain.worker.dto.response.WorkerCuisineTagResponse;
import com.homeservice.domain.worker.dto.response.WorkerDocumentResponse;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.dto.response.WorkerServiceTagResponse;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.entity.WorkerAvailability;
import com.homeservice.domain.worker.entity.WorkerCuisineTag;
import com.homeservice.domain.worker.entity.WorkerDocument;
import com.homeservice.domain.worker.entity.WorkerServiceTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkerMapper {

	@Mapping(source = "isMobileVerified", target = "isMobileVerified")
	WorkerProfileResponse toProfileResponse(Worker worker);

	@Mapping(source = "serviceType.name", target = "serviceName")
	WorkerServiceTagResponse toServiceTagResponse(WorkerServiceTag tag);

	WorkerCuisineTagResponse toCuisineTagResponse(WorkerCuisineTag tag);

	@Mapping(source = "createdAt", target = "uploadedAt")
	WorkerDocumentResponse toDocumentResponse(WorkerDocument document);

	WorkerAvailabilityResponse toAvailabilityResponse(WorkerAvailability availability);

	List<WorkerServiceTagResponse> toServiceTagResponseList(List<WorkerServiceTag> tags);

	List<WorkerCuisineTagResponse> toCuisineTagResponseList(List<WorkerCuisineTag> tags);

	List<WorkerDocumentResponse> toDocumentResponseList(List<WorkerDocument> docs);

	List<WorkerAvailabilityResponse> toAvailabilityResponseList(List<WorkerAvailability> slots);
}
