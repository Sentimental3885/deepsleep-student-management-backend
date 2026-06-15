package com.deepsleep.data.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EndCourseBatchDTO {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Valid
    @NotEmpty(message = "成绩项不能为空")
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull(message = "学生ID不能为空")
        private Long studentId;

        @NotNull(message = "成绩不能为空")
        @Digits(integer = 3, fraction = 2, message = "分数格式：XXX.XX")
        @DecimalMax(value = "100.0")
        @DecimalMin(value = "0.0")
        private Double score;
    }
}
