package com.deepsleep.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dssm.email")
public record EmailProperties(
        String from
) {
}
