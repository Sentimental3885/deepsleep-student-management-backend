package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.dto.VerifyContactDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.UserProfileVO;
import com.deepsleep.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    /**
     * 获取学生个人信息
     */
    @RequireLogin
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        return Result.success(userService.getProfile());
    }

    /**
     * 更新手机号/邮箱 联系方式
     * @param dto 选填手机号与邮箱（不填不更新）
     */
    @RequireLogin
    @PutMapping("/contact")
    public Result<Void> updateContact(@RequestBody @Valid VerifyContactDTO dto) {
        userService.updateContact(dto);
        return Result.success();
    }

    /**
     * 更新密码
     * @param dto 旧密码+新密码
     */
    @RequireLogin
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        userService.updatePassword(dto);
        return Result.success();
    }

    /**
     * 更新学生个人信息
     */
    @RequireRole(RoleEnum.STUDENT)
    @PutMapping("/student")
    public Result<Void> updateStudentInfo(@RequestBody UpdateStudentDTO dto) {
        userService.updateStudentInfo(dto);
        return Result.success();
    }
}
