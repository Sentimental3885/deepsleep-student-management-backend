package com.deepsleep.data.vo;

import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.SelectionStatus;
import lombok.Data;

@Data
public class SelectionVO {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private String code;
    private Long capacity;
    private Long size;
    private String semester;
    private Double credit;
    private CourseStatus courseStatus;
    private SelectionStatus selectionStatus;
    private Double score;
}
