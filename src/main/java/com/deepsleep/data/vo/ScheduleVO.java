package com.deepsleep.data.vo;

import lombok.Data;

@Data
public class ScheduleVO {
    private Long id;
    private Long courseId;
    private String courseName;
    private String teacherName;
    private String teacherAvatar;
    private Integer weekday;
    private Integer section;
    private Integer startWeek;
    private Integer endWeek;
    private Long classroomId;
    private String classroomName;
}
