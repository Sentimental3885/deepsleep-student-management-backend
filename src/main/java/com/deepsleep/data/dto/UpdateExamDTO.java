package com.deepsleep.data.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateExamDTO {
    @NotNull
    private Integer type;
    @NotNull private LocalDateTime examTime;
    @NotNull @Min(1) private Integer duration;
    @NotNull private Long classroomId;
    @NotNull private Long invigilatorId;
    private String remark;
}
