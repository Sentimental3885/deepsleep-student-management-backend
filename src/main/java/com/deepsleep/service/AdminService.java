package com.deepsleep.service;

import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.dto.AddTeacherDTO;


public interface AdminService {
    void addTeacher(AddTeacherDTO dto);
    void addStudent(AddStudentDTO dto);
    void resetPassword(Long userId);
    void deleteUser(Long userId);
}
