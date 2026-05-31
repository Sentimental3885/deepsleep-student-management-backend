package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamVO {
    private Long id;
    private Long courseId;
    private String courseName;
    private Integer type;
    private String typeName;// 期中/期末/补考
    private LocalDateTime examTime;
    private Integer duration;
    private Long classroomId;
    private String classroomName;
    private Long invigilatorId;
    private String invigilatorName;
    private String invigilatorAvatar;
    private String remark;
}
