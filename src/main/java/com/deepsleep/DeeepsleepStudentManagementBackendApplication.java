package com.deepsleep;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@MapperScan("com.deepsleep.mapper")
@ConfigurationPropertiesScan("com.deepsleep.config")
public class DeeepsleepStudentManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeeepsleepStudentManagementBackendApplication.class, args);
    }

}
