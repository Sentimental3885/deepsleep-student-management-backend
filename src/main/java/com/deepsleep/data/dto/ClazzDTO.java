package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClazzDTO {
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50)
    private String name;

    @NotNull(message = "所属学院不能为空")
    private Long deptId;

    @NotNull(message = "所属专业不能为空")
    private Long majorId;

    @NotNull(message = "年级不能为空")
    private Long grade;
}