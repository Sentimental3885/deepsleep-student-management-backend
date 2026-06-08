package com.deepsleep.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepseekProperties {
    private String key;
    private String url;
    private String model;
    private Integer maxTokens;
}
