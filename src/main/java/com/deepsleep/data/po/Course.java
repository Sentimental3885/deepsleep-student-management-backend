package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.deepsleep.data.enums.CourseStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;
    private Long teacherId;
    private Long capacity;
    private String code;
    private String semester;
    private Double credit;
    private CourseStatus status;
    private String introduction;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
