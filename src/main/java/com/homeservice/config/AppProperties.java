package com.homeservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@Validated
public class AppProperties {

	@NotNull
	private Jwt jwt = new Jwt();

	@NotNull
	private Otp otp = new Otp();

	@NotNull
	private Admin admin = new Admin();

	@NotNull
	private Fast2sms fast2sms = new Fast2sms();

	@NotNull
	private Dispatch dispatch = new Dispatch();

	@Getter
	@Setter
	public static class Jwt {
		@NotBlank
		private String secret;
		private long expirationMs = 86400000L;
		private long refreshExpirationMs = 604800000L;
	}

	@Getter
	@Setter
	public static class Otp {
		private int expiryMinutes = 10;
		private int maxPerHour = 3;
		private int maxAttempts = 5;
	}

	@Getter
	@Setter
	public static class Admin {
		@NotBlank
		private String secret;
	}

	@Getter
	@Setter
	public static class Fast2sms {
		private String apiKey;
		private boolean enabled = false;
	}

	@Getter
	@Setter
	public static class Dispatch {
		private double defaultRadiusKm = 3.0;
		private int maxRetries = 3;
		private int alertTimeoutSeconds = 30;
	}

	// Add these nested classes to AppProperties

	@NotNull
	private Razorpay razorpay = new Razorpay();

	@NotNull
	private Booking booking = new Booking();

	@Getter
	@Setter
	public static class Razorpay {
		private String keyId;
		private String keySecret;
		private boolean enabled = false;
	}

	@Getter
	@Setter
	public static class Booking {
		// free cancel if > this many minutes before slot
		private int cancelFreeMinutes = 30;
		// fee charged for late cancel (in rupees)
		private int cancelFee = 50;
		// auto complete after N minutes
		// if customer does not confirm
		private int autoCompleteMinutes = 5;
	}
}