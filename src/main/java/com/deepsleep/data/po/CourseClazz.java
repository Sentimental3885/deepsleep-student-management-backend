package com.deepsleep.data.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseClazz {
    private Long id;
    private Long courseId;
    private Long clazzId;
    private LocalDateTime createTime;
}
