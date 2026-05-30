package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class AdminUpdateStudentDTO {
    @NotNull(message = "学院不能为空")
    private Long deptId;

    @NotNull(message = "专业不能为空")
    private Long majorId;

    @NotNull(message = "班级不能为空")
    private Long clazzId;

    private String position;
    private LocalDate entryDate;
}