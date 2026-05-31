package com.deepsleep.data.vo;

import lombok.Data;

@Data
public class ScheduleVO {
    private Long courseId;
    private String courseName;
    private String teacherName;
    private String teacherAvatar;
    private Integer weekday;// 星期几
    private Integer section;// 节次段
    private Integer startWeek;// 开始周
    private Integer endWeek;// 结束周
    private Long classroomId;
    private String classroomName;
}
