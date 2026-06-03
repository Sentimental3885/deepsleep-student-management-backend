package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeptDTO {
    @NotBlank(message = "学院名称不能为空")
    @Size(max = 20, message = "学院名称不超过20字")
    private String name;
}