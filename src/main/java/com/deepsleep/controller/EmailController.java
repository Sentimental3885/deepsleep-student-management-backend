package com.deepsleep.controller;

import com.deepsleep.data.dto.SendCodeDTO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    /**
     * 发送验证码
     */
    @PostMapping("/code")
    public Result<Void> sendCode(@RequestBody @Valid SendCodeDTO dto) {
        emailService.sendVerifyCode(dto.getEmail());
        return Result.success();
    }
}
