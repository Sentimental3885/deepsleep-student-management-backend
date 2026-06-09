package com.deepsleep.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dssm.ai")
public class DeepseekProperties {
    private String key;
    private String url;
    private String model;
    private Integer maxTokens;
}
