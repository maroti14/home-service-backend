package com.homeservice.domain.auth.dto.response;



import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpSentResponse {
    private String mobile;
    private String message;
    private int expiresInMinutes;
}
