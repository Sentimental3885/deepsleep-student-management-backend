package com.deepsleep.infrastructure.sms;

public interface SmsSender {

    /**
     * 向指定手机号发送验证码
     * @param phone 手机号
     * @param smsCode 验证码
     */
    void send(String phone, String smsCode);
}
