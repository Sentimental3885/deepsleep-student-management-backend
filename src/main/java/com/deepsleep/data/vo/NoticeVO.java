package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}