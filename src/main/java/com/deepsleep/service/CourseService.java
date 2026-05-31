package com.deepsleep.service;

import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.ScheduleDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;

import java.util.List;

public interface CourseService {

    Result<Void> addCourse(AddCourseDTO dto);

    Result<CourseVO> getDetail(Long cid);

    Result<Void> deleteCourse(Long cid);

    Result<Void> updateCourse(Long cid, UpdateCourseDTO dto);

    Result<List<ScheduleVO>> getScheduleByCourse(Long cid);

    Result<Void> addSchedule(Long cid, ScheduleDTO dto);

    Result<Void> deleteSchedule(Long cid, Long scid);

    Result<Void> updateSchedule(Long cid, Long scid, ScheduleDTO dto);

    void verifyTeacher(Long tid, Long cid);

}
