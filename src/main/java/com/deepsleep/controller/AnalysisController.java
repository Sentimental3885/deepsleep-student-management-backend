package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.GradeAnalysisVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * 调用deepseek根据该学生成绩档案进行教学分析
     * @return 分析数据（已关闭流式输出）
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/me")
    public Result<GradeAnalysisVO> analyzeMyGrades() {
        return Result.success(analysisService.analyzeMyGrades());
    }

    /**
     * 教学分析的历史记录（list）
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/me/history")
    public Result<List<GradeAnalysisVO>> getMyAnalysisHistory() {
        return Result.success(analysisService.getMyAnalysisHistory());
    }
}