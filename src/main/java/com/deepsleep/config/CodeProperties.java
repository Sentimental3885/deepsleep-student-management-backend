package com.deepsleep.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dssm.code")
public record CodeProperties(
        boolean enabled,
        String smsProvider,
        Long intervalLockExpireSeconds,
        Long codeExpireSeconds,
        Long maxFailNumber,
        String targetHashSecret
) {
}
