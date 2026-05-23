package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.dto.AddTeacherDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员权限操作
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 创建学生
     */
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/student")
    public Result<Void> addStudent(@RequestBody @Valid AddStudentDTO dto){
        adminService.addStudent(dto);
        return Result.success();
    }

    /**
     * 创建教师
     */
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/teacher")
    public Result<Void> addTeacher(@RequestBody @Valid AddTeacherDTO dto) {
        adminService.addTeacher(dto);
        return Result.success();
    }

    /**
     * 重置用户密码为初始格式
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/user/{userId}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        adminService.resetPassword(userId);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/user/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return Result.success();
    }

}
