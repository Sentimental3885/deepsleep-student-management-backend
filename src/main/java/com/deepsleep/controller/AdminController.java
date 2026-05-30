package com.deepsleep.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.annotation.Log;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.*;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.po.OperationLog;
import com.deepsleep.data.vo.AdminUserDetailVO;
import com.deepsleep.data.vo.AdminUserVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.mapper.OperationLogMapper;
import com.deepsleep.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员权限操作有关接口
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OperationLogMapper operationLogMapper;

    /**
     * 创建学生账号
     */
    @Log("创建学生账号")
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/student")
    public Result<Void> addStudent(@RequestBody @Valid AddStudentDTO dto){
        adminService.addStudent(dto);
        return Result.success();
    }

    /**
     * 创建教师账号
     */
    @Log("创建教师账号")
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/teacher")
    public Result<Void> addTeacher(@RequestBody @Valid AddTeacherDTO dto) {
        adminService.addTeacher(dto);
        return Result.success();
    }

    /**
     * 重置用户密码为初始格式
     */
    @Log("重置用户密码")
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/user/{userId}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        adminService.resetPassword(userId);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Log("删除用户")
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

    /**
     * 管理员修改用户信息
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/user/{userId}")
    public Result<Void> updateUser(@PathVariable Long userId,
                                   @RequestBody @Valid AdminUpdateUserDTO dto) {
        adminService.updateUser(userId, dto);
        return Result.success();
    }

    /**
     * 管理员修改学生用户信息
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/student/{userId}")
    public Result<Void> updateStudent(@PathVariable Long userId,
                                      @RequestBody @Valid AdminUpdateStudentDTO dto) {
        adminService.updateStudent(userId, dto);
        return Result.success();
    }

    /**
     * 管理员修改教师用户信息
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/teacher/{userId}")
    public Result<Void> updateTeacher(@PathVariable Long userId,
                                      @RequestBody @Valid AdminUpdateTeacherDTO dto) {
        adminService.updateTeacher(userId, dto);
        return Result.success();
    }

    /**
     * 管理员查看操作日志
     */
    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/log/list")
    public Result<Page<OperationLog>> getLogList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<OperationLog> page = operationLogMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<OperationLog>()
                        .orderByDesc(OperationLog::getCreateTime)
        );
        return Result.success(page);
    }
}
