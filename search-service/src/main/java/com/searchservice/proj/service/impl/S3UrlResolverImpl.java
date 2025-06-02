package com.searchservice.proj.service.impl;

import com.searchservice.proj.service.S3UrlResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class S3UrlResolverImpl implements S3UrlResolver {


    @Value("${cloud.aws.s3.bucket-name:your-default-bucket}")
    private String bucketName;
    @Value("${cloud.aws.region.static:your-default-region}")
    private String region;


    @Override
    public String resolveUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return null;
        }

        log.warn("S3UrlResolverImpl.resolveUrl is using a placeholder URL format. Configure for public or pre-signed URLs.");
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3Key);
    }
}