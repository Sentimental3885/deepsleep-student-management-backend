package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleDTO {
    @NotNull(message = "开课日不能为空")
    private Integer weekday;
    @NotNull(message = "节次不能为空")
    private Integer section;
    @NotNull(message = "开课初周不能为空")
    private Integer startWeek;
    @NotNull(message = "开课末周不能为空")
    private Integer endWeek;
    @NotNull(message = "教室id不能为空")
    private Long rid;
}
