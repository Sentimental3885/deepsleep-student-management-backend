package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddStudentDTO {
    //学号异于学生id，故用ssid区分
    @NotNull(message = "学号不能为空")
    private Long ssid;
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotNull(message = "性别不能为空")
    private Integer gender;
    @NotNull(message = "学院id不能为空")
    private Long did;
    @NotNull(message = "专业id不能为空")
    private Long mid;
    @NotNull(message = "班级id不能为空")
    private Long zid;
    private LocalDate entryDate;
}
