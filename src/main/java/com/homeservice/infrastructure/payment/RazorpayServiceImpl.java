package com.homeservice.infrastructure.payment;

import com.homeservice.config.AppProperties;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayServiceImpl implements RazorpayService {

	private final AppProperties props;

	@Override
	public String createOrder(BigDecimal amount, String currency, String receipt) {

		if (!props.getRazorpay().isEnabled()) {
			// return mock order ID in dev
			String mockId = "order_mock_" + System.currentTimeMillis();
			log.info("Razorpay disabled. " + "Mock order | id={}", mockId);
			return mockId;
		}

		try {
			RazorpayClient client = new RazorpayClient(props.getRazorpay().getKeyId(),
					props.getRazorpay().getKeySecret());

			JSONObject orderRequest = new JSONObject();

			// Razorpay amount is in paise
			// (multiply by 100)
			orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
			orderRequest.put("currency", currency != null ? currency : "INR");
			orderRequest.put("receipt", receipt);

			Order order = client.orders.create(orderRequest);

			log.info("Razorpay order created | " + "id={} amount={}", order.get("id"), amount);

			return order.get("id");

		} catch (Exception e) {
			log.error("Razorpay order failed | " + "error={}", e.getMessage());
			throw new RuntimeException("Payment gateway error. " + "Please try again.");
		}
	}

	@Override
	public boolean verifyWebhookSignature(String payload, String signature, String secret) {
		try {
			return verifyHmac(payload, signature, secret);
		} catch (Exception e) {
			log.error("Webhook verify failed | " + "error={}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
		try {
			String data = orderId + "|" + paymentId;
			return verifyHmac(data, signature, props.getRazorpay().getKeySecret());
		} catch (Exception e) {
			log.error("Payment verify failed | " + "error={}", e.getMessage());
			return false;
		}
	}

	private boolean verifyHmac(String data, String signature, String secret)
			throws NoSuchAlgorithmException, InvalidKeyException {

		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		mac.init(secretKey);

		byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}

		return hexString.toString().equals(signature);
	}
}
