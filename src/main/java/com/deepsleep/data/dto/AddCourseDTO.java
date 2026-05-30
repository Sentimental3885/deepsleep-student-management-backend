package com.deepsleep.data.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class AddCourseDTO {
    @NotBlank(message = "课程名称不能为空")
    private String name;
    @NotNull(message = "教师id不能为空")
    private Long tid;
    @Min(value = 1)
    @NotNull(message = "课程容量不能为空")
    private Long capacity;
    @NotBlank(message = "课程代码不能为空")
    private String code;
    @NotBlank(message = "开课学期不能为空")
    private String semester;
    @NotNull(message = "课程学分不能为空")
    @Digits(integer = 1, fraction = 1, message = "课程学分形如X.X")
    @DecimalMin(value = "0.0")
    private Double credit;
    @NotNull(message = "课程状态不能为空")
    @Max(value = 1)
    @Min(value = 0)
    private Integer status;
    private String introduction;

    @NotNull(message = "开课班级不能为空")
    private List<Long> zids;
}
