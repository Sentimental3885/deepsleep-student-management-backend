package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.deepsleep.data.enums.SelectionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CourseSelection {

    //自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;
    private Long courseId;
    private Double score;
    private SelectionStatus status;
    private LocalDateTime createTime;

    public CourseSelection(Long studentId, Long courseId, Double score, SelectionStatus status) {
        //id必须为null才能触发MybatisPlus的自增主键填充
        id = null;
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
        this.status = status;
        this.createTime = LocalDateTime.now();
    }
}
