package com.deepsleep.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dssm.code")
public record CodeProperties(
        Long intervalLockExpireSeconds,
        Long codeExpireSeconds,
        Long maxFailNumber,
        String targetHashSecret
) {
}
