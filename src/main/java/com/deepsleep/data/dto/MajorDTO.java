package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MajorDTO {
    @NotBlank(message = "专业名称不能为空")
    @Size(max = 20)
    private String name;

    @NotNull(message = "所属学院不能为空")
    private Long deptId;
}