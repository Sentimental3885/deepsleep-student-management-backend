package com.deepsleep.service;

import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherProfileVO;

import java.util.List;

public interface TeacherService {
    TeacherProfileVO getTeacherProfile();
    void updateTeacherInfo(UpdateTeacherDTO dto);
    List<TeacherCourseVO> getMyCourses();
}
