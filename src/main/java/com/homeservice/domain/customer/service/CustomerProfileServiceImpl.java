package com.homeservice.domain.customer.service;

import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.common.util.SanitizationUtils;
import com.homeservice.domain.auth.repository.UserRepository;
import com.homeservice.domain.customer.dto.request.UpdateCustomerProfileRequest;
import com.homeservice.domain.customer.dto.response.CustomerProfileResponse;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.mapper.CustomerMapper;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.infrastructure.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

	private final CustomerRepository customerRepo;
	private final UserRepository userRepo;
	private final S3StorageService s3Service;
	private final CustomerMapper customerMapper;

	private static final String PROFILE_PHOTO_FOLDER = "profile-photos";

	@Override
	@Transactional(readOnly = true)
	public CustomerProfileResponse getProfile(Long customerId) {

		Customer customer = findCustomer(customerId);
		return customerMapper.toProfileResponse(customer);
	}

	@Override
	@Transactional
	public CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest req) {

		Customer customer = findCustomer(customerId);

		// update name if provided
		if (req.getName() != null && !req.getName().isBlank()) {
			customer.setName(SanitizationUtils.sanitizeName(req.getName()));
		}

		// update email if provided
		if (req.getEmail() != null && !req.getEmail().isBlank()) {

			String newEmail = SanitizationUtils.sanitizeEmail(req.getEmail());

			// check email not taken by someone else
			if (userRepo.existsByEmailAndIdNot(newEmail, customerId)) {
				throw new InvalidInputException("Email already in use.");
			}

			customer.setEmail(newEmail);
		}

		// update city if provided
		if (req.getCity() != null && !req.getCity().isBlank()) {
			customer.setCity(SanitizationUtils.sanitizeText(req.getCity()));
		}

		customerRepo.save(customer);

		log.info("Profile updated | " + "customerId={}", customerId);

		return customerMapper.toProfileResponse(customer);
	}

	@Override
	@Transactional
	public CustomerProfileResponse uploadProfilePhoto(Long customerId, MultipartFile photo) {

		Customer customer = findCustomer(customerId);

		// delete old photo if exists
		if (customer.getProfilePhotoUrl() != null) {
			s3Service.deleteFile(customer.getProfilePhotoUrl());
		}

		String photoUrl = s3Service.uploadFile(photo, PROFILE_PHOTO_FOLDER);

		customer.setProfilePhotoUrl(photoUrl);
		customerRepo.save(customer);

		log.info("Profile photo uploaded | " + "customerId={}", customerId);

		return customerMapper.toProfileResponse(customer);
	}

	// ── Private helpers ───────────────────────────────

	private Customer findCustomer(Long customerId) {
		return customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
	}
}
