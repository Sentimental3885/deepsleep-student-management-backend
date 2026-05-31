package com.deepsleep.data.vo;

import lombok.Data;

@Data
public class TeacherCourseVO {
    private Long id;
    private String name;
    private String code;
    private String semester;
    private Long capacity;
    private Long size;//目前选课人数
    private Double credit;
    private Integer status;
}
