package com.deepsleep.service.Impl;

import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.service.EmailService;
import com.deepsleep.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    private static final String CODE_PREFIX = "verify_code:";
    private static final long CODE_TTL = 5;

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public void sendVerifyCode(String email) {
        String code = String.format("%06d",new Random().nextInt(1000000));
        // 存入Redistemplate,5分钟过期
        redisUtil.set(CODE_PREFIX+email,code,CODE_TTL, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("【学生管理系统】邮箱验证码");
        message.setText("您的验证码为：" + code + "\n5分钟内有效，请勿泄露给他人。");
        mailSender.send(message);

    }

    @Override
    public void verifyCode(String email, String code) {
        String key = CODE_PREFIX + email;
        Object stored = redisUtil.get(key);

        if(stored==null) throw new BusinessException(ResultCode.EMAIL_CODE_EXPIRED);
        if(!stored.toString().equals(code)) throw new BusinessException(ResultCode.EMAIL_CODE_ERROR);

        redisUtil.delete(key);
    }
}
