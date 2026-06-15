package com.deepsleep.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.po.GradeAnalysis;
import com.deepsleep.data.vo.GradeAnalysisVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.CourseMapper;
import com.deepsleep.mapper.CourseSelectionMapper;
import com.deepsleep.mapper.GradeAnalysisMapper;
import com.deepsleep.service.AnalysisService;
import com.deepsleep.service.DeepseekService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final GradeAnalysisMapper analysisMapper;
    private final CourseSelectionMapper selectionMapper;
    private final CourseMapper courseMapper;
    private final DeepseekService deepseekService;

    private static final String System_Prompt = """
            你是一位经验丰富的学业辅导老师。请根据学生的成绩数据进行全面分析，包括：
                    1. 整体学业水平评估
                    2. 优势学科与薄弱学科分析
                    3. 成绩变化趋势（按学期）
                    4. 学习建议
                    请用简洁、鼓励性的语言，输出 400 字以内的分析报告，使用清晰的小标题分段。
            """;

    @Override
    public GradeAnalysisVO analyzeMyGrades() {
        Long userId = UserContext.getUserId();

        // 先查该学生结课的所有成绩
        List<CourseSelection> selections = selectionMapper.selectList(new LambdaQueryWrapper<CourseSelection>()
                .eq(CourseSelection::getStudentId,userId)
                .eq(CourseSelection::getStatus, SelectionStatus.OVER)
                .isNotNull(CourseSelection::getScore)
        );
        if(selections.isEmpty()) throw new BusinessException(ResultCode.NO_GRADES_TO_ANALYZE);
        List<Map<String, Object>> scoreData = selections.stream().map(selection->{
            Course course = courseMapper.selectById(selection.getCourseId());
            Map<String, Object> data = new HashMap<>();
            data.put("courseName", course.getName());
            data.put("semester", course.getSemester());
            data.put("credit", course.getCredit());
            data.put("score", selection.getScore());
            return data;
        }).toList();

        String currentSnapshot = JSON.toJSONString(scoreData);

        // 查最新一次的分析记录，若成绩没变化，直接返回上次结果
        GradeAnalysis latest = analysisMapper.selectOne(new LambdaQueryWrapper<GradeAnalysis>()
                        .eq(GradeAnalysis::getStudentId, userId)
                        .orderByDesc(GradeAnalysis::getCreateTime)
                        .last("LIMIT 1")
        );
        if (latest!=null && currentSnapshot.equals(latest.getScoreSnapshot())) {
            return convertToVO(latest);
        }

        // 调deepseekAPI分析
        String userMessage = "以下为该学生的成绩数据，请按要求进行分析:\n"+currentSnapshot;
        String analysisContent = deepseekService.chat(System_Prompt, userMessage);

        GradeAnalysis analysis = new GradeAnalysis();
        analysis.setStudentId(userId);
        analysis.setContent(analysisContent);
        analysis.setScoreSnapshot(currentSnapshot);
        analysis.setCreateTime(LocalDateTime.now());
        analysisMapper.insert(analysis);

        return convertToVO(analysis);


    }


    private GradeAnalysisVO convertToVO(GradeAnalysis analysis) {
        GradeAnalysisVO vo = new GradeAnalysisVO();
        vo.setId(analysis.getId());
        vo.setContent(analysis.getContent());
        vo.setCreateTime(analysis.getCreateTime());
        return vo;
    }

    @Override
    public List<GradeAnalysisVO> getMyAnalysisHistory() {
        Long userId = UserContext.getUserId();
        List<GradeAnalysis> list = analysisMapper.selectList(new LambdaQueryWrapper<GradeAnalysis>()
                        .eq(GradeAnalysis::getStudentId, userId)
                        .orderByDesc(GradeAnalysis::getCreateTime)
        );
        return list.stream().map(this::convertToVO).toList();
    }
}
