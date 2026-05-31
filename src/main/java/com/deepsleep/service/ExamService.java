package com.deepsleep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.AddExamDTO;
import com.deepsleep.data.dto.UpdateExamDTO;
import com.deepsleep.data.vo.ExamVO;

import java.util.List;

public interface ExamService {
    void addExam(AddExamDTO  dto);
    void updateExam(Long id, UpdateExamDTO dto);
    void deleteExam(Long id);
    ExamVO getExamDetail(Long id);
    Page<ExamVO> getAllExams(int pageNum, int pageSize);
    List<ExamVO> getMyExamsAsStudent();
    List<ExamVO> getMyExamsAsInvigilator();
}
