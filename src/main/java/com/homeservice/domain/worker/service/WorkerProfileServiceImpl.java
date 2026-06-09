package com.homeservice.domain.worker.service;

import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.common.util.SanitizationUtils;
import com.homeservice.domain.auth.repository.UserRepository;
import com.homeservice.domain.worker.dto.request.UpdateWorkerProfileRequest;
import com.homeservice.domain.worker.dto.request.WorkerOnlineStatusRequest;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.mapper.WorkerMapper;
import com.homeservice.domain.worker.repository.WorkerRepository;
import com.homeservice.infrastructure.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerProfileServiceImpl implements WorkerProfileService {

	private final WorkerRepository workerRepo;
	private final UserRepository userRepo;
	private final S3StorageService s3Service;
	private final WorkerMapper workerMapper;

	private static final String PHOTO_FOLDER = "worker-photos";

	@Override
	@Transactional(readOnly = true)
	public WorkerProfileResponse getProfile(Long workerId) {
		return workerMapper.toProfileResponse(findWorker(workerId));
	}

	@Override
	@Transactional
	public WorkerProfileResponse updateProfile(Long workerId, UpdateWorkerProfileRequest req) {

		Worker worker = findWorker(workerId);

		if (req.getName() != null && !req.getName().isBlank()) {
			worker.setName(SanitizationUtils.sanitizeName(req.getName()));
		}

		if (req.getEmail() != null && !req.getEmail().isBlank()) {
			String email = SanitizationUtils.sanitizeEmail(req.getEmail());

			if (userRepo.existsByEmailAndIdNot(email, workerId)) {
				throw new InvalidInputException("Email already in use.");
			}
			worker.setEmail(email);
		}

		if (req.getCity() != null && !req.getCity().isBlank()) {
			worker.setCity(SanitizationUtils.sanitizeText(req.getCity()));
		}

		if (req.getBio() != null) {
			worker.setBio(SanitizationUtils.sanitizeText(req.getBio()));
		}

		if (req.getExperienceYears() != null) {
			worker.setExperienceYears(req.getExperienceYears());
		}

		workerRepo.save(worker);

		log.info("Worker profile updated | " + "workerId={}", workerId);

		return workerMapper.toProfileResponse(worker);
	}

	@Override
	@Transactional
	public WorkerProfileResponse uploadProfilePhoto(Long workerId, MultipartFile photo) {

		Worker worker = findWorker(workerId);

		if (worker.getProfilePhotoUrl() != null) {
			s3Service.deleteFile(worker.getProfilePhotoUrl());
		}

		String url = s3Service.uploadFile(photo, PHOTO_FOLDER);

		worker.setProfilePhotoUrl(url);
		workerRepo.save(worker);

		log.info("Worker photo uploaded | " + "workerId={}", workerId);

		return workerMapper.toProfileResponse(worker);
	}

	@Override
	@Transactional
	public WorkerProfileResponse updateOnlineStatus(Long workerId, WorkerOnlineStatusRequest req) {

		Worker worker = findWorker(workerId);

		// worker must be approved
		// before going online
		if (Boolean.TRUE.equals(req.getIsOnline()) && !worker.getIsApproved()) {
			throw new InvalidInputException(
					"Your profile is not approved yet. " + "Please wait for admin approval " + "before going online.");
		}

		worker.setIsOnline(req.getIsOnline());
		workerRepo.save(worker);

		log.info("Worker online status updated | " + "workerId={} isOnline={}", workerId, req.getIsOnline());

		return workerMapper.toProfileResponse(worker);
	}

	// ── Private helpers ───────────────────────────────

	public Worker findWorker(Long workerId) {
		return workerRepo.findById(workerId)
				.orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + workerId));
	}
}
