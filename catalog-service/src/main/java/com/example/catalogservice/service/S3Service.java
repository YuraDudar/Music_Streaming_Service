package com.example.catalogservice.service;

public interface S3Service {

    String getObjectUrl(String key);
    //String generatePresignedUploadUrl(String fileName, String contentType);

}