package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;
import com.deepsleep.data.vo.AvatarUpdateVO;
import com.deepsleep.data.vo.MyUserInfoVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @RequireLogin
    @GetMapping("/me")
    public Result<MyUserInfoVO> me() {
        return Result.success(userService.me());
    }

    /**
     * 更换邮箱
     * @param dto 新邮箱+旧邮箱收到的验证码
     */
    @RequireLogin
    @PutMapping("/email")
    public Result<Void> updateEmail(@RequestBody @Valid UpdateEmailDTO dto) {
        userService.updateEmail(dto);
        return Result.success();
    }

    /**
     * 更换手机号
     * @param dto 新手机号+接收到的邮箱验证码
     */
    @RequireLogin
    @PutMapping("/phone")
    public Result<Void> updatePhone(@RequestBody @Valid UpdatePhoneDTO dto) {
        userService.updatePhone(dto);
        return Result.success();
    }

    /**
     * 更新密码
     * @param dto 邮箱+验证码+新密码
     */
    @RequireLogin
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        userService.updatePassword(dto);
        return Result.success();
    }

    /**
     * 更新头像
     */
    @RequireLogin
    @PutMapping("/avatar")
    public Result<AvatarUpdateVO> updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        return Result.success(userService.updateAvatar(avatar));
    }

}
