package com.deepsleep.infrastructure.file.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aliyun.oss")
public record AliyunOSSProperties (
        String accessKeyId,
        String accessKeySecret,
        String region,
        String endpoint,
        String bucket,
        Long presignedUrlExpireSeconds
) {}

