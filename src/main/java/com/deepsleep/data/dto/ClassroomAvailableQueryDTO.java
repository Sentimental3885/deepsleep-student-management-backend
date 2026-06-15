package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassroomAvailableQueryDTO {
    @NotNull(message = "星期不能为空")
    private Integer weekday;

    @NotNull(message = "节次不能为空")
    private Integer section;

    @NotNull(message = "开始周不能为空")
    private Integer startWeek;

    @NotNull(message = "结束周不能为空")
    private Integer endWeek;

    private String semester;
}
