package com.deepsleep.data.vo;

import com.deepsleep.data.enums.SelectionStatus;
import lombok.Data;

@Data
public class CourseStudentVO {
    private Long studentId;
    private String studentName;
    private String username;// 学号
    private Double score;
    private SelectionStatus selectionStatus;
}
