package com.deepsleep.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.dto.AddTeacherDTO;
import com.deepsleep.data.dto.UserQueryDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.AdminUserDetailVO;
import com.deepsleep.data.vo.AdminUserVO;
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

    /**
     * 查询用户信息
     * @param dto dto支持不填（查所有）、角色筛选、用户名筛选、姓名模糊筛选
     */
    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/user/list")
    public Result<Page<AdminUserVO>> getUserList(@Valid UserQueryDTO dto) {
        return Result.success(adminService.getUserList(dto));
    }

    /**
     * 查用户详情
     */
    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/user/{userId}")
    public Result<AdminUserDetailVO> getUserDetail(@PathVariable Long userId) {
        return Result.success(adminService.getUserDetail(userId));
    }
}
