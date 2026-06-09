package com.homeservice.common.constant;

public final class ApiConstants {

	private ApiConstants() {
	}

	public static final String BASE = "/api/v1";
	public static final String AUTH = BASE + "/auth";
	public static final String ADMIN = BASE + "/admin";
	public static final String CUSTOMER = BASE + "/customer";
	public static final String WORKER = BASE + "/worker";

	public static final String CUSTOMER_REGISTER = AUTH + "/customer/register";
	public static final String WORKER_REGISTER = AUTH + "/worker/register";
	public static final String ADMIN_REGISTER = AUTH + "/admin/register";
	public static final String LOGIN = AUTH + "/login";
	public static final String LOGIN_SEND_OTP = AUTH + "/login/send-otp";
	public static final String LOGIN_VERIFY_OTP = AUTH + "/login/verify-otp";
	public static final String VERIFY_MOBILE = AUTH + "/verify-mobile";
	public static final String RESEND_OTP = AUTH + "/resend-otp";
	public static final String REFRESH_TOKEN = AUTH + "/refresh";
	public static final String LOGOUT = AUTH + "/logout";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
	public static final String ROLE_WORKER = "ROLE_WORKER";

	public static final int DEFAULT_PAGE = 0;
	public static final int DEFAULT_SIZE = 10;
	public static final int MAX_PAGE_SIZE = 50;

	public static final String[] SWAGGER_WHITELIST = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
			"/actuator/health", "/actuator/info" };

	// Add these to ApiConstants

	public static final String CUSTOMER_PROFILE = CUSTOMER + "/profile";
	public static final String CUSTOMER_PROFILE_PHOTO = CUSTOMER + "/profile/photo";
	public static final String CUSTOMER_ADDRESSES = CUSTOMER + "/addresses";
	public static final String CUSTOMER_REVERSE_GEOCODE = CUSTOMER + "/location/reverse-geocode";
	public static final String CUSTOMER_AUTOCOMPLETE = CUSTOMER + "/location/autocomplete";
	public static final String CUSTOMER_PLACE_DETAILS = CUSTOMER + "/location/place-details";
}