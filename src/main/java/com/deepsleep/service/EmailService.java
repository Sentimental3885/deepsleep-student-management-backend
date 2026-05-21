package com.deepsleep.service;

public interface EmailService {
    void sendVerifyCode(String email);
    void verifyCode(String email,String code);
}
