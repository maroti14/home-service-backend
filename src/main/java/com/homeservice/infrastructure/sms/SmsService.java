package com.homeservice.infrastructure.sms;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.homeservice.config.AppProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final AppProperties props;
    private final RestTemplate restTemplate =
            new RestTemplate();

    public void sendOtp(String mobile, String otp) {

        log.info("==============================");
        log.info("OTP | mobile=+91{} code={}",
                 mobile, otp);
        log.info("==============================");

        if (!props.getFast2sms().isEnabled()) {
            log.warn("SMS disabled. " +
                     "OTP printed to console only.");
            return;
        }

        try {
            String url =
                "https://www.fast2sms.com/dev/bulkV2"
                + "?authorization="
                + props.getFast2sms().getApiKey()
                + "&variables_values=" + otp
                + "&route=otp"
                + "&numbers=" + mobile;

            HttpHeaders headers = new HttpHeaders();
            headers.set("cache-control", "no-cache");

            ResponseEntity<String> response =
                restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class);

            if (response.getStatusCode()
                        .is2xxSuccessful()) {
                log.info("SMS sent | mobile=+91{}",
                         mobile);
            } else {
                log.error("SMS failed | " +
                          "status={} body={}",
                          response.getStatusCode(),
                          response.getBody());
            }
        } catch (Exception e) {
            log.error("SMS error | mobile=+91{} " +
                      "error={}", mobile,
                      e.getMessage());
        }
    }
}