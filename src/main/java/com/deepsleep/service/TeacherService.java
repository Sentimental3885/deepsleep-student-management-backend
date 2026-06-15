package com.deepsleep.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.data.dto.TeacherOptionQueryDTO;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherOptionVO;
import com.deepsleep.data.vo.TeacherProfileVO;

import java.util.List;

public interface TeacherService {
    TeacherProfileVO getTeacherProfile();
    void updateTeacherInfo(UpdateTeacherDTO dto);
    List<TeacherCourseVO> getMyCourses();
    List<ScheduleVO> getMySchedule();
    IPage<TeacherOptionVO> getTeacherOptions(TeacherOptionQueryDTO dto);
}
