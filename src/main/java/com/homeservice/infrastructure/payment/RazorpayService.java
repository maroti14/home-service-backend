package com.homeservice.infrastructure.payment;

import java.math.BigDecimal;

public interface RazorpayService {

	// create Razorpay order
	// returns Razorpay order ID
	String createOrder(BigDecimal amount, String currency, String receipt);

	// verify webhook signature
	boolean verifyWebhookSignature(String payload, String signature, String secret);

	// verify payment signature
	boolean verifyPaymentSignature(String orderId, String paymentId, String signature);
}
