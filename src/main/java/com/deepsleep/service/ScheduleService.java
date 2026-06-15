package com.deepsleep.service;

import com.deepsleep.data.dto.ClassroomScheduleQueryDTO;
import com.deepsleep.data.vo.ScheduleVO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleVO> getClassroomSchedule(Long classroomId, ClassroomScheduleQueryDTO dto);
}
