package ru.SberPo666.stream_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client = S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    private final S3Presigner s3Presigner = S3Presigner.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    public ResponseInputStream<GetObjectResponse> getS3Object(String key, String range) {
        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key);

        if (range != null && !range.isEmpty()) {
            requestBuilder.range(range);
        }

        return s3Client.getObject(requestBuilder.build());
    }

}
