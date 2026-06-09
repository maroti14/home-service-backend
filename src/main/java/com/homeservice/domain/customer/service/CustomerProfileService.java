package com.homeservice.domain.customer.service;

import com.homeservice.domain.customer.dto.request.UpdateCustomerProfileRequest;
import com.homeservice.domain.customer.dto.response.CustomerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerProfileService {

	CustomerProfileResponse getProfile(Long customerId);

	CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest req);

	CustomerProfileResponse uploadProfilePhoto(Long customerId, MultipartFile photo);
}
