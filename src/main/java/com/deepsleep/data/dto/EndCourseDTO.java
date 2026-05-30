package com.deepsleep.data.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EndCourseDTO {

    @NotNull(message = "学生序号不能为空")
    private Long sid;

    @NotNull(message = "课序号不能为空")
    private Long cid;

    @NotNull(message = "结课成绩不能为空")
    @Digits(integer = 3, fraction = 2, message = "分数格式：XXX.XX")
    private Double score;
}
