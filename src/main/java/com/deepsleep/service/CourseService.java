package com.deepsleep.service;

import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;

public interface CourseService {

    Result<Void> addCourse(AddCourseDTO dto);

    Result<CourseVO> getDetail(Long cid);

    Result<Void> deleteCourse(Long cid);

    Result<Void> updateCourse(Long cid, UpdateCourseDTO dto);

    void verifyTeacher(Long tid, Long cid);

}
