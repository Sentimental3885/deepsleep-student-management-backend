package com.deepsleep.service;

import com.deepsleep.data.dto.ClassroomDTO;
import com.deepsleep.data.dto.ClassroomAvailableQueryDTO;
import com.deepsleep.data.vo.ClassroomVO;

import java.util.List;

public interface ClassroomService {
    void addClassroom(ClassroomDTO dto);
    void updateClassroom(Long id, ClassroomDTO dto);
    void deleteClassroom(Long id);
    List<ClassroomVO> listClassrooms();
    List<ClassroomVO> listAvailableClassrooms(ClassroomAvailableQueryDTO dto);
}
