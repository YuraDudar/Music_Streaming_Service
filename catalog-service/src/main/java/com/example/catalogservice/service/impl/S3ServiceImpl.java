package com.example.catalogservice.service.impl;

import com.example.catalogservice.service.S3Service;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Template s3Template;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String getObjectUrl(String key) {
        if (key == null || key.isBlank()) {
            log.warn("Requested S3 URL for null or blank key.");
            return null;
        }
        try {
            S3Resource resource = s3Template.download(bucketName, key);
            URL url = resource.getURL();
            log.debug("Generated S3 URL: {} for key: {}", url, key);
            return url.toString();
        } catch (Exception e) {
            log.error("Error generating S3 URL for key '{}' in bucket '{}': {}", key, bucketName, e.getMessage());

            return null;
        }
    }

    //@Override
    public String generatePresignedUploadUrl(String fileName, String contentType) {
        if (fileName == null || fileName.isBlank()) {
            log.warn("Requested S3 pre-signed upload URL for null or blank fileName.");
            return null;
        }
        try {
            Map<String, String> metadata = new HashMap<>();
            if (contentType != null && !contentType.isBlank()) {
                metadata.put("Content-Type", contentType);
            }

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
            String url = presignedPutObjectRequest.url().toString();
            log.info("Generated pre-signed S3 upload URL: {} for fileName: {}", url, fileName);
            return url;
        } catch (Exception e) {
            log.error("Error generating pre-signed S3 upload URL for fileName '{}': {}", fileName, e.getMessage(), e);
            return null;
        }
    }
}