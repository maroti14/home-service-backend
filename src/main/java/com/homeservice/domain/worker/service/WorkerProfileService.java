package com.homeservice.domain.worker.service;

import com.homeservice.domain.worker.dto.request.UpdateWorkerProfileRequest;
import com.homeservice.domain.worker.dto.request.WorkerOnlineStatusRequest;
import com.homeservice.domain.worker.dto.response.WorkerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface WorkerProfileService {

	WorkerProfileResponse getProfile(Long workerId);

	WorkerProfileResponse updateProfile(Long workerId, UpdateWorkerProfileRequest req);

	WorkerProfileResponse uploadProfilePhoto(Long workerId, MultipartFile photo);

	WorkerProfileResponse updateOnlineStatus(Long workerId, WorkerOnlineStatusRequest req);
}
