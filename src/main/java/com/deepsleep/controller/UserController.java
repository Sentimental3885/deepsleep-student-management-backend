package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.data.dto.*;
import com.deepsleep.data.vo.AvatarUpdateVO;
import com.deepsleep.data.vo.MyUserInfoVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    /**
     * 获取用户个人信息
     */
    @RequireLogin
    @GetMapping("/me")
    public Result<MyUserInfoVO> me() {
        return Result.success(userService.me());
    }

    /**
     * 更换邮箱时获取验证码
     * @param sendEmailCodeDTO 新绑定的邮箱
     */
    @RequireLogin
    @PostMapping("/email/code")
    public Result<Void> updateEmailCode(@Validated @RequestBody SendEmailCodeDTO sendEmailCodeDTO) {
        userService.updateEmailCode(sendEmailCodeDTO);
        return Result.success();
    }

    /**
     * 更换邮箱
     * @param dto 新邮箱 + 新邮箱收到的验证码
     */
    @RequireLogin
    @PutMapping("/email")
    public Result<Void> updateEmail(@RequestBody @Valid UpdateEmailDTO dto) {
        userService.updateEmail(dto);
        return Result.success();
    }

    /**
     * 更换手机号时获取手机验证码
     */
    @RequireLogin
    @PostMapping("/phone/code")
    public Result<Void> updatePhoneCode(@Validated @RequestBody SendPhoneCodeDTO sendPhoneCodeDTO) {
        userService.updatePhoneCode(sendPhoneCodeDTO);
        return Result.success();
    }

    /**
     * 更换手机号
     * @param dto 新手机号 + 新手机号接收到的验证码
     */
    @RequireLogin
    @PutMapping("/phone")
    public Result<Void> updatePhone(@RequestBody @Valid UpdatePhoneDTO dto) {
        userService.updatePhone(dto);
        return Result.success();
    }

    /**
     * 更新密码时获取验证码
     * 向登录账号所绑定的邮箱发送验证码。若当前登陆账号未绑定邮箱，则返回错误信息。
     */
    @RequireLogin
    @PostMapping("/password/code")
    public Result<Void> updatePasswordCode() {
        userService.updatePasswordCode();
        return Result.success();
    }

    /**
     * 更新密码
     * @param dto 验证码+新密码
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
