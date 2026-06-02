package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("grade_analysis")
public class GradeAnalysis {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String content;// AI分析的内容
    private String scoreSnapshot;// 成绩快照，Json格式，判断成绩是否发生变化
    private LocalDateTime createTime;
}