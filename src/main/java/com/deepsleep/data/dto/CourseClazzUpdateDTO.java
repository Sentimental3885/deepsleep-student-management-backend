package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CourseClazzUpdateDTO {
    @NotEmpty(message = "开课班级不能为空")
    private List<@NotNull Long> clazzIds;
}
