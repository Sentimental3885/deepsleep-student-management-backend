package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherInfoVO {
    private Long deptId;
    private String deptName;
    private String title;
    private LocalDate entryDate;
}