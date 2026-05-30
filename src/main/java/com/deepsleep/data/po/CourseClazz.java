package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CourseClazz {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long clazzId;
    private LocalDateTime createTime;
}
