package com.deepsleep.infrastructure.email.model;

public record UpdatePersonalInfoMailModel(
        String email,
        String code,
        long expireMinutes,
        String operationTime,
        int currentYear
) {
}
