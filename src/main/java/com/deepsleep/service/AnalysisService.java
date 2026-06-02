package com.deepsleep.service;

import com.deepsleep.data.vo.GradeAnalysisVO;

import java.util.List;

public interface AnalysisService {
    GradeAnalysisVO analyzeMyGrades();
    List<GradeAnalysisVO> getMyAnalysisHistory();
}
