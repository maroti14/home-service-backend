package com.homeservice.common.util;

import org.springframework.util.StringUtils;

public final class SanitizationUtils {

	private SanitizationUtils() {
	}

	public static String sanitize(String input) {
		if (!StringUtils.hasText(input))
			return input;
		return input.trim().replaceAll("<[^>]*>", "").replaceAll("\0", "").replaceAll("\\s+", " ");
	}

	public static String sanitizeEmail(String email) {
		if (!StringUtils.hasText(email))
			return email;
		return email.trim().toLowerCase();
	}

	public static String sanitizeMobile(String mobile) {
		if (!StringUtils.hasText(mobile))
			return mobile;
		String digits = mobile.trim().replaceAll("[^0-9]", "");
		if (digits.length() == 12 && digits.startsWith("91")) {
			digits = digits.substring(2);
		}
		return digits;
	}

	public static String sanitizeName(String name) {
		if (!StringUtils.hasText(name))
			return name;
		return sanitize(name).replaceAll("[^a-zA-Z\\s.'\\-]", "").trim();
	}

	public static String sanitizeCityCode(String code) {
		if (!StringUtils.hasText(code))
			return code;
		return code.trim().toUpperCase().replaceAll("[^A-Z]", "");
	}

	public static String sanitizeText(String text) {
		if (!StringUtils.hasText(text))
			return text;
		return text.trim().replaceAll("<[^>]*>", "").replaceAll("\0", "").replaceAll("\\s+", " ");
	}
}
