package com.deepsleep;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.deepsleep.mapper")
public class DeeepsleepStudentManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeeepsleepStudentManagementBackendApplication.class, args);
    }

}
