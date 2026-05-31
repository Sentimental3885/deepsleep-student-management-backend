package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Integer type;
    private LocalDateTime examTime;
    private Integer duration;
    private Long classroomId;
    private Long invigilatorId;// 监考老师ID
    private String remark;// 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
