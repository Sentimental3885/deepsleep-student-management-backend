package com.deepsleep.infrastructure.code.sms;


import com.deepsleep.infrastructure.code.store.CodeScene;

public interface SmsVerificationService {

    /**
     * 给用户发送验证码
     * @param scene 业务类型对象，用于指定需要验证码的业务
     * @param phone 用户手机号
     */
    void sendSmsCode(CodeScene scene, String phone);

    /**
     * 校验用户输入的验证码是否正确，校验失败抛出异常
     * @param scene 业务类型对象，用于指定需要验证码的业务
     * @param phone 用户手机号
     * @param code 用户输入的验证码
     */
    void checkSmsCode(CodeScene scene, String phone, String code);
}
