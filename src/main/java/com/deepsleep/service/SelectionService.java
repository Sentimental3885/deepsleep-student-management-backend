package com.deepsleep.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.data.dto.CourseStudentQueryDTO;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.EndCourseBatchDTO;
import com.deepsleep.data.dto.ScoreQueryDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScoreVO;
import com.deepsleep.data.vo.SelectionCheckVO;

import java.util.List;

public interface SelectionService {
    Result<IPage<CourseVO>> showAvailableList(Long sid, SelectionQueryDTO dto);

    Result<Void> pickCourse(Long sid, Long cid);

    Result<Void> dropCourse(Long sid, Long cid);

    Result<Void> endCourse(Long tid, EndCourseDTO dto);

    Result<Void> endCourseBatch(Long tid, EndCourseBatchDTO dto);

    Result<SelectionCheckVO> checkSelectable(Long sid, Long cid);

    Result<IPage<CourseVO>> showSelectedList(Long sid, SelectionQueryDTO dto);

    Long currentSize(Long cid);

    Result<List<CourseStudentVO>> showCourseStudents(Long tid, Long cid);

    Result<IPage<CourseStudentVO>> showCourseStudents(Long operatorId, Integer role, Long cid, CourseStudentQueryDTO dto);

    Result<IPage<ScoreVO>> showScoreList(Long sid, ScoreQueryDTO dto);

    Result<ScoreVO> getScoreDetail(Long sid, Long cid);
}
