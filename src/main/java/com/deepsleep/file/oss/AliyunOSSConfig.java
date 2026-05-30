package com.deepsleep.file.oss;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliyunOSSProperties.class)
public class AliyunOSSConfig {

    @Bean(destroyMethod = "close")
    public OSSClient ossClient(AliyunOSSProperties ossProperties) {
        CredentialsProvider provider =
                new StaticCredentialsProvider(ossProperties.accessKeyId(), ossProperties.accessKeySecret());
        return OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(ossProperties.region())
                .endpoint(ossProperties.endpoint())
                .useCName(true)
                .build();
    }
}
