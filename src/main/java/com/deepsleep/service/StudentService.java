package com.deepsleep.service;

import com.deepsleep.mapper.StudentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Resource
    private StudentMapper studentMapper;



}
