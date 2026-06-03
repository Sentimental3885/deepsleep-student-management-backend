package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.ClassroomDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.ClassroomVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classroom")
public class ClassroomController {

    private final ClassroomService classroomService;

    /**
     * 查看所有教室
     */
    @RequireLogin
    @GetMapping("/list")
    public Result<List<ClassroomVO>> listClassrooms() {
        return Result.success(classroomService.listClassrooms());
    }

    /**
     * 创建教室
     */
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping
    public Result<Void> addClassroom(@RequestBody @Valid ClassroomDTO dto) {
        classroomService.addClassroom(dto);
        return Result.success();
    }

    /**
     * 更新教室信息
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/{id}")
    public Result<Void> updateClassroom(@PathVariable Long id,
                                        @RequestBody @Valid ClassroomDTO dto) {
        classroomService.updateClassroom(id, dto);
        return Result.success();
    }

    /**
     * 删除教室
     */
    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/{id}")
    public Result<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return Result.success();
    }
}
