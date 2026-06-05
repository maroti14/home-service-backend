package com.homeservice.domain.auth.service;



import com.homeservice.common.enums.OtpPurpose;
import com.homeservice.common.enums.UserRole;
import com.homeservice.common.exception.InvalidInputException;
import com.homeservice.common.exception.ResourceAlreadyExistsException;
import com.homeservice.common.exception.ResourceNotFoundException;
import com.homeservice.domain.auth.dto.request.*;
import com.homeservice.domain.auth.dto.response.AuthResponse;
import com.homeservice.domain.auth.dto.response.OtpSentResponse;
import com.homeservice.domain.auth.entity.Role;
import com.homeservice.domain.auth.entity.User;
import com.homeservice.domain.auth.repository.RoleRepository;
import com.homeservice.domain.customer.entity.Customer;
import com.homeservice.domain.customer.repository.CustomerRepository;
import com.homeservice.domain.worker.entity.Worker;
import com.homeservice.domain.worker.repository.WorkerRepository;
import com.homeservice.security.JwtUtils;
import com.homeservice.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepo;
    private final WorkerRepository workerRepo;
    private final RoleRepository roleRepo;
    private final OtpService otpService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
 // Add at top of AuthService
    @Value("${app.admin.secret}")
    private String adminSecret;
    
    
 // Add this method
    @Transactional
    public AuthResponse registerAdmin(
            AdminRegisterRequest req) {

        // validate secret key
        if (!adminSecret.equals(req.getAdminSecret())) {
            throw new InvalidInputException(
                "Invalid admin secret key.");
        }

        // check no existing user with same email
        if (customerRepo.existsByEmail(req.getEmail()) ||
            workerRepo.existsByEmail(req.getEmail())) {
            throw new ResourceAlreadyExistsException(
                "Email already registered.");
        }

        Role role = findRole(UserRole.ROLE_ADMIN);

        // Admin stored as Customer entity
        // (same users table, different role)
        Customer admin = Customer.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase().trim())
                .mobile(req.getMobile().trim())
                .password(passwordEncoder.encode(
                              req.getPassword()))
                .role(role)
                .isActive(true)
                .isMobileVerified(true) // admin pre-verified
                .build();

        customerRepo.save(admin);

        UserDetailsImpl details = new UserDetailsImpl(admin);
        String token = jwtUtils.generateToken(details);

        log.info("Admin registered: {}", req.getEmail());

        return buildAuthResponse(details, token);
    }

    // ── Register Customer ────────────────────────────────
    @Transactional
    public OtpSentResponse registerCustomer(
            CustomerRegisterRequest req) {

        validateNewUser(req.getEmail(), req.getMobile(),
                        false); // false = customer

        Role role = findRole(UserRole.ROLE_CUSTOMER);

        Customer customer = Customer.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase().trim())
                .mobile(req.getMobile().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .city(req.getCity())
                .isActive(true)
                .isMobileVerified(false)
                .build();

        customerRepo.save(customer);

        // send OTP for mobile verification
        otpService.generateAndSend(
                req.getMobile(),
                OtpPurpose.MOBILE_VERIFICATION);

        log.info("Customer registered: {}",
                 req.getEmail());

        return OtpSentResponse.builder()
                .mobile(req.getMobile())
                .message("Registration successful. " +
                         "OTP sent to your mobile " +
                         "for verification.")
                .expiresInMinutes(
                    otpService.getExpiryMinutes())
                .build();
    }

    // ── Register Worker ──────────────────────────────────
    @Transactional
    public OtpSentResponse registerWorker(
            WorkerRegisterRequest req) {

        validateNewUser(req.getEmail(), req.getMobile(),
                        true); // true = worker

        Role role = findRole(UserRole.ROLE_WORKER);

        Worker worker = Worker.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase().trim())
                .mobile(req.getMobile().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .city(req.getCity())
                .isActive(true)
                .isMobileVerified(false)
                .isApproved(false)
                .build();

        workerRepo.save(worker);

        otpService.generateAndSend(
                req.getMobile(),
                OtpPurpose.MOBILE_VERIFICATION);

        log.info("Worker registered: {}", req.getEmail());

        return OtpSentResponse.builder()
                .mobile(req.getMobile())
                .message("Registration successful. " +
                         "OTP sent to your mobile " +
                         "for verification.")
                .expiresInMinutes(
                    otpService.getExpiryMinutes())
                .build();
    }

    // ── Verify Mobile After Registration ─────────────────
    @Transactional
    public String verifyMobile(VerifyMobileRequest req) {

        // verify the OTP first
        otpService.verifyOtp(
                req.getMobile(),
                req.getOtp(),
                OtpPurpose.MOBILE_VERIFICATION);

        // find and update the user (customer or worker)
        User user = findUserByMobile(req.getMobile());
        user.setIsMobileVerified(true);
        saveUser(user);

        log.info("Mobile verified for: {}",
                 req.getMobile());

        return "Mobile number verified successfully. " +
               "You can now login.";
    }

    // ── Email + Password Login ───────────────────────────
    public AuthResponse loginWithPassword(
            String email, String password) {

        Authentication authentication =
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    email, password));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        UserDetailsImpl userDetails =
            (UserDetailsImpl) authentication.getPrincipal();

        // enforce mobile verification before login
        User user = findUserByMobile(
            userDetails.getMobile());
        if (!user.getIsMobileVerified()) {
            throw new InvalidInputException(
                "Please verify your mobile number " +
                "before logging in.");
        }

        String token = jwtUtils.generateToken(
                userDetails);

        return buildAuthResponse(userDetails, token);
    }

    // ── Send OTP for Login ───────────────────────────────
    public OtpSentResponse sendLoginOtp(
            SendOtpRequest req) {

        // confirm user exists
        findUserByMobile(req.getMobile());

        otpService.generateAndSend(
                req.getMobile(),
                OtpPurpose.LOGIN);

        return OtpSentResponse.builder()
                .mobile(req.getMobile())
                .message("OTP sent to your mobile number.")
                .expiresInMinutes(
                    otpService.getExpiryMinutes())
                .build();
    }

    // ── Login With OTP ───────────────────────────────────
    @Transactional
    public AuthResponse loginWithOtp(
            LoginWithOtpRequest req) {

        // verify OTP
        otpService.verifyOtp(
                req.getMobile(),
                req.getOtp(),
                OtpPurpose.LOGIN);

        User user = findUserByMobile(req.getMobile());

        if (!user.getIsMobileVerified()) {
            throw new InvalidInputException(
                "Please verify your mobile number first.");
        }

        if (!user.getIsActive()) {
            throw new InvalidInputException(
                "Your account is deactivated. " +
                "Contact support.");
        }

        UserDetailsImpl userDetails =
                new UserDetailsImpl(user);

        String token = jwtUtils.generateToken(userDetails);

        return buildAuthResponse(userDetails, token);
    }

    // ── Resend OTP ───────────────────────────────────────
    public OtpSentResponse resendOtp(
            SendOtpRequest req) {

        otpService.generateAndSend(
                req.getMobile(),
                req.getPurpose());

        return OtpSentResponse.builder()
                .mobile(req.getMobile())
                .message("New OTP sent to your mobile.")
                .expiresInMinutes(
                    otpService.getExpiryMinutes())
                .build();
    }

    // ── Private Helpers ──────────────────────────────────

    private void validateNewUser(String email,
                                  String mobile,
                                  boolean isWorker) {
        if (isWorker) {
            if (workerRepo.existsByEmail(email))
                throw new ResourceAlreadyExistsException(
                    "Email already registered.");
            if (workerRepo.existsByMobile(mobile))
                throw new ResourceAlreadyExistsException(
                    "Mobile already registered.");
        } else {
            if (customerRepo.existsByEmail(email))
                throw new ResourceAlreadyExistsException(
                    "Email already registered.");
            if (customerRepo.existsByMobile(mobile))
                throw new ResourceAlreadyExistsException(
                    "Mobile already registered.");
        }
    }

    private Role findRole(UserRole roleEnum) {
        return roleRepo.findByName(roleEnum.name())
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Role not found: " +
                        roleEnum.name()));
    }

    private User findUserByMobile(String mobile) {
        // check customer first, then worker
        return customerRepo.findByMobile(mobile)
                .map(c -> (User) c)
                .orElseGet(() ->
                    workerRepo.findByMobile(mobile)
                        .map(w -> (User) w)
                        .orElseThrow(() ->
                            new ResourceNotFoundException(
                                "No account found with " +
                                "mobile: " + mobile)));
    }

    private void saveUser(User user) {
        if (user instanceof Customer c)
            customerRepo.save(c);
        else if (user instanceof Worker w)
            workerRepo.save(w);
    }

    private AuthResponse buildAuthResponse(
            UserDetailsImpl details, String token) {

        return AuthResponse.builder()
                .userId(details.getUserId())
                .name(details.getName())
                .email(details.getUsername())
                .mobile(details.getMobile())
                .role(details.getRole())
                .accessToken(token)
                .mobileVerified(
                    details.isMobileVerified())
                .build();
    }
}
