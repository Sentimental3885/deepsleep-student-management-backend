package com.deepsleep.service;

import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.vo.TeacherProfileVO;

public interface TeacherService {
    TeacherProfileVO getTeacherProfile();
    void updateTeacherInfo(UpdateTeacherDTO dto);
}
