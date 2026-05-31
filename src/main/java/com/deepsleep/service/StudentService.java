package com.deepsleep.service;


import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.StudentProfileVO;

import java.util.List;

public interface StudentService {


    StudentProfileVO getStudentProfile();
    void updateStudentInfo(UpdateStudentDTO dto);
    List<ScheduleVO> getMySchedule();
}
