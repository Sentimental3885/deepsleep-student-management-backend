package com.deepsleep.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeRandomGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateCode() {
        int code = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
