package com.searchservice.proj.service;

public interface S3UrlResolver {
    String resolveUrl(String s3Key);
}