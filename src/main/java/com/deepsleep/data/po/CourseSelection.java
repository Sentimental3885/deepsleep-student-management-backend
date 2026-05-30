package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.deepsleep.data.enums.SelectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourseSelection {

    //自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;
    private Long courseId;
    private Double score;
    private SelectionStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
