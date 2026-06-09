package com.homeservice.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.common.enums.UserRole;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.common.util.SanitizationUtils;
import com.homeservice.config.AppProperties;
import com.homeservice.domain.auth.dto.request.AdminRegisterRequest;
import com.homeservice.domain.auth.dto.request.CustomerRegisterRequest;
import com.homeservice.domain.auth.dto.request.LoginRequest;
import com.homeservice.domain.auth.dto.request.LoginWithOtpRequest;
import com.homeservice.domain.auth.dto.request.LogoutRequest;
import com.homeservice.domain.auth.dto.request.RefreshTokenRequest;
import com.homeservice.domain.auth.dto.request.SendOtpRequest;
import com.homeservice.domain.auth.dto.request.VerifyMobileRequest;
import com.homeservice.domain.auth.dto.request.WorkerRegisterRequest;
import com.homeservice.domain.auth.dto.response.AuthResponse;
import com.homeservice.domain.auth.dto.response.OtpSentResponse;
import com.homeservice.domain.auth.dto.response.TokenResponse;
import com.homeservice.domain.auth.entity.Role;
import com.homeservice.domain.auth.entity.User;
import com.homeservice.domain.auth.mapper.AuthMapper;
import com.homeservice.domain.auth.repository.RoleRepository;
import com.homeservice.domain.auth.repository.UserRepository;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.repository.WorkerRepository;
import com.homeservice.security.JwtUtils;
import com.homeservice.security.UserDetailsImpl;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepo;
	private final CustomerRepository customerRepo;
	private final WorkerRepository workerRepo;
	private final RoleRepository roleRepo;
	private final OtpService otpService;
	private final RefreshTokenService refreshService;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authManager;
	private final AppProperties props;
	private final AuthMapper authMapper;

	@Override
	@Transactional
	public OtpSentResponse registerCustomer(CustomerRegisterRequest req, String deviceInfo, String ip) {

		String email = SanitizationUtils.sanitizeEmail(req.getEmail());
		String mobile = SanitizationUtils.sanitizeMobile(req.getMobile());
		String name = SanitizationUtils.sanitizeName(req.getName());

		checkUnique(email, mobile);

		Role role = findRole(UserRole.ROLE_CUSTOMER);

		Customer customer = Customer.builder().name(name).email(email).mobile(mobile)
				.password(passwordEncoder.encode(req.getPassword())).role(role).city(req.getCity()).isActive(true)
				.isMobileVerified(false).build();

		customerRepo.save(customer);

		otpService.generateAndSend(mobile, OtpPurpose.MOBILE_VERIFICATION);

		log.info("Customer registered | email={}", email);

		return otpSent(mobile, "Registration successful. " + "OTP sent for verification.");
	}

	@Override
	@Transactional
	public OtpSentResponse registerWorker(WorkerRegisterRequest req, String deviceInfo, String ip) {

		String email = SanitizationUtils.sanitizeEmail(req.getEmail());
		String mobile = SanitizationUtils.sanitizeMobile(req.getMobile());
		String name = SanitizationUtils.sanitizeName(req.getName());

		checkUnique(email, mobile);

		Role role = findRole(UserRole.ROLE_WORKER);

		Worker worker = Worker.builder().name(name).email(email).mobile(mobile)
				.password(passwordEncoder.encode(req.getPassword())).role(role).city(req.getCity()).isActive(true)
				.isMobileVerified(false).isApproved(false).build();

		workerRepo.save(worker);

		otpService.generateAndSend(mobile, OtpPurpose.MOBILE_VERIFICATION);

		log.info("Worker registered | email={}", email);

		return otpSent(mobile, "Registration successful. " + "OTP sent for verification.");
	}

	@Override
	@Transactional
	public AuthResponse registerAdmin(AdminRegisterRequest req, String deviceInfo, String ip) {

		if (!props.getAdmin().getSecret().equals(req.getAdminSecret())) {
			throw new InvalidInputException("Invalid admin secret.");
		}

		String email = SanitizationUtils.sanitizeEmail(req.getEmail());
		String mobile = SanitizationUtils.sanitizeMobile(req.getMobile());

		checkUnique(email, mobile);

		Role role = findRole(UserRole.ROLE_ADMIN);

		Customer admin = Customer.builder().name(SanitizationUtils.sanitizeName(req.getName())).email(email)
				.mobile(mobile).password(passwordEncoder.encode(req.getPassword())).role(role).isActive(true)
				.isMobileVerified(true).build();

		customerRepo.save(admin);

		log.info("Admin registered | email={}", email);

		return buildResponse(admin, deviceInfo, ip);
	}

	@Override
	@Transactional
	public String verifyMobile(VerifyMobileRequest req) {

		otpService.verifyOtp(req.getMobile(), req.getOtp(), OtpPurpose.MOBILE_VERIFICATION);

		User user = findByMobile(req.getMobile());
		user.setIsMobileVerified(true);
		userRepo.save(user);

		log.info("Mobile verified | mobile={}", req.getMobile());

		return "Mobile verified. You can now login.";
	}

	@Override
	public AuthResponse loginWithPassword(LoginRequest req, String deviceInfo, String ip) {

		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(auth);

		UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();

		if (!details.isMobileVerified()) {
			throw new InvalidInputException("Please verify your mobile first.");
		}

		User user = userRepo.findByEmail(req.getEmail()).orElseThrow();

		log.info("Login | email={}", req.getEmail());

		return buildResponse(user, deviceInfo, ip);
	}

	@Override
	public OtpSentResponse sendLoginOtp(SendOtpRequest req) {

		findByMobile(req.getMobile());
		otpService.generateAndSend(req.getMobile(), OtpPurpose.LOGIN);

		return otpSent(req.getMobile(), "OTP sent to your mobile.");
	}

	@Override
	@Transactional
	public AuthResponse loginWithOtp(LoginWithOtpRequest req, String deviceInfo, String ip) {

		otpService.verifyOtp(req.getMobile(), req.getOtp(), OtpPurpose.LOGIN);

		User user = findByMobile(req.getMobile());

		if (!user.getIsMobileVerified()) {
			throw new InvalidInputException("Please verify your mobile first.");
		}
		if (!user.getIsActive()) {
			throw new InvalidInputException("Account deactivated.");
		}

		log.info("OTP login | mobile={}", req.getMobile());

		return buildResponse(user, deviceInfo, ip);
	}

	@Override
	public OtpSentResponse resendOtp(SendOtpRequest req) {
		otpService.generateAndSend(req.getMobile(), req.getPurpose());
		return otpSent(req.getMobile(), "New OTP sent.");
	}

	@Override
	public TokenResponse refreshToken(RefreshTokenRequest req) {
		return refreshService.refresh(req.getRefreshToken());
	}

	@Override
	public void logout(LogoutRequest req) {
		refreshService.revoke(req.getRefreshToken());
	}

	@Override
	public void logoutAllDevices(Long userId) {
		refreshService.revokeAll(userId);
	}

	// ── Helpers ───────────────────────────────────────

	private AuthResponse buildResponse(User user, String deviceInfo, String ip) {

		UserDetailsImpl details = new UserDetailsImpl(user);

		String accessToken = jwtUtils.generateAccessToken(details);

		String refreshToken = refreshService.create(user, deviceInfo, ip);

		long expiresIn = props.getJwt().getExpirationMs() / 1000;

		return authMapper.fromDetails(details, accessToken, refreshToken, expiresIn);
	}

	private void checkUnique(String email, String mobile) {
		if (userRepo.existsByEmail(email)) {
			throw new ResourceAlreadyExistsException("Email already registered.");
		}
		if (userRepo.existsByMobile(mobile)) {
			throw new ResourceAlreadyExistsException("Mobile already registered.");
		}
	}

	private Role findRole(UserRole roleEnum) {
		return roleRepo.findByName(roleEnum.name())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleEnum.name()));
	}

	private User findByMobile(String mobile) {
		return userRepo.findByMobile(mobile)
				.orElseThrow(() -> new ResourceNotFoundException("No account found for " + "mobile: " + mobile));
	}

	private OtpSentResponse otpSent(String mobile, String message) {
		return OtpSentResponse.builder().mobile(mobile).message(message)
				.expiresInMinutes(props.getOtp().getExpiryMinutes()).build();
	}
}