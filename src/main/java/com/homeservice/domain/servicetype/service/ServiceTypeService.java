package com.homeservice.domain.servicetype.service;

import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.servicetype.dto.request.CreateServiceTypeRequest;
import com.homeservice.domain.servicetype.dto.request.UpdateServiceTypeRequest;
import com.homeservice.domain.servicetype.dto.response.ServiceTypeResponse;
import com.homeservice.domain.servicetype.entity.ServiceType;
import com.homeservice.domain.servicetype.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceTypeService {

	private final ServiceTypeRepository serviceTypeRepo;

	// ── Get all active services ───────────────────────
	@Transactional(readOnly = true)
	public List<ServiceTypeResponse> getAllActive() {
		return serviceTypeRepo.findByIsActiveTrueOrderByDisplayOrderAsc().stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	// ── Get all services (admin) ──────────────────────
	@Transactional(readOnly = true)
	public List<ServiceTypeResponse> getAll() {
		return serviceTypeRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	// ── Get by id ─────────────────────────────────────
	@Transactional(readOnly = true)
	public ServiceTypeResponse getById(Long id) {
		return toResponse(findById(id));
	}

	// ── Create service type (admin) ───────────────────
	@Transactional
	public ServiceTypeResponse create(CreateServiceTypeRequest req) {

		if (serviceTypeRepo.existsByServiceKey(req.getServiceKey())) {
			throw new ResourceAlreadyExistsException("Service already exists: " + req.getServiceKey());
		}

		ServiceType service = ServiceType.builder().serviceKey(req.getServiceKey()).name(req.getName())
				.description(req.getDescription()).iconName(req.getIconName()).colorCode(req.getColorCode())
				.bgColorCode(req.getBgColorCode()).minSlots(req.getMinSlots() != null ? req.getMinSlots() : 1)
				.maxSlots(req.getMaxSlots() != null ? req.getMaxSlots() : 6).slotDurationMinutes(30)
				.requiresCuisineTag(req.getRequiresCuisineTag() != null ? req.getRequiresCuisineTag() : false)
				.requiresPhotos(req.getRequiresPhotos() != null ? req.getRequiresPhotos() : true)
				.isNew(req.getIsNew() != null ? req.getIsNew() : false).isActive(true)
				.displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0).build();

		serviceTypeRepo.save(service);
		log.info("Service type created: {}", service.getName());

		return toResponse(service);
	}

	// ── Update service type (admin) ───────────────────
	@Transactional
	public ServiceTypeResponse update(Long id, UpdateServiceTypeRequest req) {

		ServiceType service = findById(id);

		if (req.getName() != null)
			service.setName(req.getName());
		if (req.getDescription() != null)
			service.setDescription(req.getDescription());
		if (req.getIconName() != null)
			service.setIconName(req.getIconName());
		if (req.getColorCode() != null)
			service.setColorCode(req.getColorCode());
		if (req.getBgColorCode() != null)
			service.setBgColorCode(req.getBgColorCode());
		if (req.getMinSlots() != null)
			service.setMinSlots(req.getMinSlots());
		if (req.getMaxSlots() != null)
			service.setMaxSlots(req.getMaxSlots());
		if (req.getRequiresCuisineTag() != null)
			service.setRequiresCuisineTag(req.getRequiresCuisineTag());
		if (req.getRequiresPhotos() != null)
			service.setRequiresPhotos(req.getRequiresPhotos());
		if (req.getIsNew() != null)
			service.setIsNew(req.getIsNew());
		if (req.getIsActive() != null)
			service.setIsActive(req.getIsActive());
		if (req.getDisplayOrder() != null)
			service.setDisplayOrder(req.getDisplayOrder());

		serviceTypeRepo.save(service);
		return toResponse(service);
	}

	public ServiceType findById(Long id) {
		return serviceTypeRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service type not found: " + id));
	}

	private ServiceTypeResponse toResponse(ServiceType s) {
		return ServiceTypeResponse.builder().id(s.getId()).serviceKey(s.getServiceKey()).name(s.getName())
				.description(s.getDescription()).iconName(s.getIconName()).colorCode(s.getColorCode())
				.bgColorCode(s.getBgColorCode()).minSlots(s.getMinSlots()).maxSlots(s.getMaxSlots())
				.slotDurationMinutes(s.getSlotDurationMinutes()).requiresCuisineTag(s.getRequiresCuisineTag())
				.requiresPhotos(s.getRequiresPhotos()).isNew(s.getIsNew()).isActive(s.getIsActive())
				.displayOrder(s.getDisplayOrder()).build();
	}
}
