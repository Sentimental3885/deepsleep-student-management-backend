package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassroomDTO {
    @NotBlank(message = "教室名称不能为空")
    @Size(max = 50, message = "教室名称不超过50字")
    private String name;
}