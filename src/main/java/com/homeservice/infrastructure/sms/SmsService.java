package com.homeservice.infrastructure.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SmsService {

	@Value("${msg91.auth-key}")
	private String authKey;

	@Value("${msg91.sender-id}")
	private String senderId;

	@Value("${msg91.template-id}")
	private String templateId;

	@Value("${msg91.base-url}")
	private String baseUrl;

	@Value("${msg91.enabled:false}")
	private boolean msg91Enabled;

	private final RestTemplate restTemplate = new RestTemplate();

	// ── Send OTP via MSG91 ────────────────────────────
	public void sendOtp(String mobileNumber, String otp) {

		// always log OTP in console
		// so you can test without SMS in dev
		log.info("=================================");
		log.info("OTP for +91{} → {}", mobileNumber, otp);
		log.info("=================================");

		if (!msg91Enabled) {
			log.warn("MSG91 disabled. " + "OTP logged to console only. " + "Set msg91.enabled=true " + "for real SMS.");
			return;
		}

		try {
			String url = baseUrl + "/flow/";

			// MSG91 Send OTP API payload
			Map<String, Object> payload = new HashMap<>();
			payload.put("template_id", templateId);
			payload.put("short_url", "0");
			payload.put("realTimeResponse", "1");

			// recipient mobile
			Map<String, String> recipient = new HashMap<>();
			recipient.put("mobiles", "91" + mobileNumber);
			recipient.put("otp", otp);

			payload.put("recipients", List.of(recipient));

			// headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("authkey", authKey);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("MSG91 OTP sent " + "to +91{}. Response: {}", mobileNumber, response.getBody());
			} else {
				log.error("MSG91 failed. " + "Status: {} Body: {}", response.getStatusCode(), response.getBody());
			}

		} catch (Exception e) {
			log.error("MSG91 SMS failed " + "for +91{}: {}", mobileNumber, e.getMessage());
			// do not throw exception
			// OTP is already saved in DB
			// user can use resend OTP
		}
	}
}