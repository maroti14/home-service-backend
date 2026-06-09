package com.homeservice.domain.worker.dto.response;

import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.common.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkerDocumentResponse {

	private Long id;
	private DocumentType documentType;
	private String fileUrl;
	private DocumentStatus status;
	private String rejectionReason;
	private String originalFileName;
	private LocalDateTime uploadedAt;
}
