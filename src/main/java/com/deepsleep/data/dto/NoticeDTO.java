package com.deepsleep.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 用于发布与修改
@Data
public class NoticeDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不超过100字")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}