package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.StudentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/student")
@RestController
public class StudentController {
    @Resource
    private StudentService studentService;

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/add")
    public Result<Void> addStudent(@RequestBody @Valid AddStudentDTO dto){
        return studentService.addStudent(dto);
    }
}
