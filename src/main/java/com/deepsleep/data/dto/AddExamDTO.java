package com.deepsleep.data.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddExamDTO {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "考试类型不能为空")
    @Min(value = 1) @Max(value = 3)
    private Integer type;

    @NotNull(message = "考试时间不能为空")
    @Future(message = "考试时间必须晚于当前时间")
    private LocalDateTime examTime;

    @NotNull(message = "考试时长不能为空")
    @Min(value = 1, message = "考试时长最少1分钟")
    private Integer duration;

    @NotNull(message = "考场不能为空")
    private Long classroomId;

    @NotNull(message = "监考教师不能为空")
    private Long invigilatorId;

    private String remark;
}
