package com.homeservice.common.exception;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private Instant timeStamp;
	private int status;
	private String error;
	private String message;
	private String path;
}
