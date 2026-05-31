package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.AddExamDTO;
import com.deepsleep.data.dto.UpdateExamDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.ExamVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    /**
     * 创建考试
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PostMapping
    public Result<Void> addExam(@RequestBody @Valid AddExamDTO dto) {
        examService.addExam(dto);
        return Result.success();
    }

    /**
     * 更新考试
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PutMapping("/{id}")
    public Result<Void> updateExam(@PathVariable Long id,
                                   @RequestBody @Valid UpdateExamDTO dto) {
        examService.updateExam(id, dto);
        return Result.success();
    }

    /**
     * 删除考试
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @DeleteMapping("/{id}")
    public Result<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.success();
    }

    /**
     * 考试详情
     * @return 考试信息
     */
    @RequireLogin
    @GetMapping("/{id}")
    public Result<ExamVO> getExamDetail(@PathVariable Long id) {
        return Result.success(examService.getExamDetail(id));
    }
}
