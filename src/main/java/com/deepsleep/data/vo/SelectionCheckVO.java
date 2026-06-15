package com.deepsleep.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class SelectionCheckVO {
    private Boolean selectable;
    private String reasonCode;
    private String reason;
    private List<ScheduleVO> conflictSchedules;
}
