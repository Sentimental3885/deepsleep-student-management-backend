package com.deepsleep.data.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCourseDTO {
    @NotNull(message = "课程容量不能为空")
    private Long capacity;
    @NotNull(message = "课程状态不能为空")
    @Max(value = 1)
    @Min(value = 0)
    private Integer status;
    private String introduction;
}
