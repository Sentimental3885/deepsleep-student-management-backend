package com.deepsleep.data.dto;

import lombok.Data;

@Data
public class ClassroomScheduleQueryDTO {
    private String semester;
    private Integer startWeek;
    private Integer endWeek;
    private Integer weekday;
}
