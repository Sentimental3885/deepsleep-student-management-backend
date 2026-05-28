package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 查看教师个人信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @GetMapping("/profile")
    public Result<TeacherProfileVO> getTeacherProfile() {
        return Result.success(teacherService.getTeacherProfile());
    }

    /**
     * 更新教师个人信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @PutMapping("/profile")
    public Result<Void> updateTeacherInfo(@RequestBody UpdateTeacherDTO dto) {
        teacherService.updateTeacherInfo(dto);
        return Result.success();
    }
}
