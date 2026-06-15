package com.deepsleep.data.vo;

import com.deepsleep.data.enums.SelectionStatus;
import lombok.Data;

import java.util.List;

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
    private Integer status;
    private String introduction;
    private List<ClazzVO> clazzes;
    private Boolean selectable;
    private String unselectableReason;
    private SelectionStatus mySelectionStatus;
}
