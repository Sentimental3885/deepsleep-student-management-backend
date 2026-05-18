package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentInfoVO {
    private Long deptId;
    private String deptName;
    private Long majorId;
    private String majorName;
    private Long clazzId;
    private String clazzName;
    private String position;
    private LocalDate entryDate;
}
