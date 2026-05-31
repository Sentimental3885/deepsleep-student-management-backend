package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_schedule")
public class CourseSchedule {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Integer weekday;
    private Integer section;
    private Integer startWeek;
    private Integer endWeek;
    private Long classroomId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}