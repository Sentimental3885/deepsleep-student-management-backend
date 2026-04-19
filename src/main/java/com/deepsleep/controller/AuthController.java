package com.deepsleep.controller;

import com.deepsleep.data.dto.LoginDTO;
import com.deepsleep.data.vo.LoginVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 登录
     * @param loginDTO 用户名，密码
     * @return logi
     *
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO){
        return Result.success(authService.login(loginDTO));
    }
}
