package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class AdminUpdateTeacherDTO {
    @NotNull(message = "学院不能为空")
    private Long deptId;

    private String title;
    private LocalDate entryDate;
}