package com.homeservice.domain.worker.entity;

import com.homeservice.common.base.BaseEntity;
import com.homeservice.common.enums.DocumentStatus;
import com.homeservice.common.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "worker_documents", indexes = { @Index(name = "idx_doc_worker_id", columnList = "worker_id"),
		@Index(name = "idx_doc_type", columnList = "worker_id, document_type") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerDocument extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id", nullable = false)
	private Worker worker;

	@Enumerated(EnumType.STRING)
	@Column(name = "document_type", nullable = false, length = 30)
	private DocumentType documentType;

	// S3 URL of the uploaded document
	@Column(nullable = false, length = 500)
	private String fileUrl;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(nullable = false, length = 20)
	private DocumentStatus status = DocumentStatus.PENDING;

	// admin rejection reason
	@Column(length = 300)
	private String rejectionReason;

	// original file name
	@Column(length = 200)
	private String originalFileName;
}
