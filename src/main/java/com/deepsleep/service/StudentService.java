package com.deepsleep.service;

import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.vo.Result;

public interface StudentService {
    Result<Void> addStudent(AddStudentDTO dto);
}
