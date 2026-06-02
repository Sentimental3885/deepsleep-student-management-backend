package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeAnalysisVO {
    private Long id;
    private String content;// 只需返回生成内容
    private LocalDateTime createTime;
}