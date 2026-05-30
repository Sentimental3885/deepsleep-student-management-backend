package com.deepsleep.data.vo;

import com.deepsleep.data.enums.CourseStatus;
import lombok.Data;

@Data
public class CourseVO {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private String teacherAvatar;
    private String code;
    private Long capacity;
    private Long size;
    private String semester;
    private Double credit;
    private CourseStatus status;
    private String introduction;
}
