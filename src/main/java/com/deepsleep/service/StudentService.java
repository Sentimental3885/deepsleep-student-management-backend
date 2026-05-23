package com.deepsleep.service;


import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.vo.StudentProfileVO;

public interface StudentService {


    StudentProfileVO getStudentProfile();
    void updateStudentInfo(UpdateStudentDTO dto);
}
