package com.homeservice.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class S3StorageService {

	private final S3Client s3Client;

	@Value("${aws.s3.bucket-name:}")
	private String bucketName;

	@Value("${aws.s3.base-url:}")
	private String baseUrl;

	@Value("${aws.s3.enabled:false}")
	private boolean s3Enabled;

	public S3StorageService() {
		// S3Client will be null if not configured
		// handled gracefully below
		S3Client client;
		try {
			client = S3Client.builder().build();
		} catch (Exception e) {
			client = null;
		}
		this.s3Client = client;
	}

	// ── Upload file ───────────────────────────────────
	public String uploadFile(MultipartFile file, String folder) {

		if (!s3Enabled) {
			log.warn("S3 disabled. " + "Returning mock URL.");
			return "https://mock-s3.com/" + folder + "/" + file.getOriginalFilename();
		}

		validateFile(file);

		String fileName = buildFileName(folder, file.getOriginalFilename());

		try {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(fileName)
					.contentType(file.getContentType()).contentLength(file.getSize()).build();

			s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			String url = baseUrl + "/" + fileName;

			log.info("File uploaded | key={}", fileName);

			return url;

		} catch (IOException e) {
			log.error("S3 upload failed | " + "folder={} error={}", folder, e.getMessage());
			throw new RuntimeException("File upload failed. " + "Please try again.");
		}
	}

	// ── Delete file ───────────────────────────────────
	public void deleteFile(String fileUrl) {

		if (!s3Enabled || fileUrl == null)
			return;

		try {
			// extract key from full URL
			String key = fileUrl.replace(baseUrl + "/", "");

			s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());

			log.info("File deleted | key={}", key);

		} catch (Exception e) {
			log.error("S3 delete failed | " + "url={} error={}", fileUrl, e.getMessage());
		}
	}

	// ── Private helpers ───────────────────────────────

	private void validateFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty.");
		}

		// max 5MB
		long maxSize = 5L * 1024 * 1024;
		if (file.getSize() > maxSize) {
			throw new IllegalArgumentException("File size must be under 5MB.");
		}

		String contentType = file.getContentType();
		if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
			throw new IllegalArgumentException("Only image and PDF files allowed.");
		}
	}

	private String buildFileName(String folder, String originalName) {

		String extension = "";
		if (originalName != null && originalName.contains(".")) {
			extension = originalName.substring(originalName.lastIndexOf("."));
		}

		return folder + "/" + UUID.randomUUID() + extension;
	}
}
