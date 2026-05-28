package com.deepsleep.data.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserQueryDTO {
    private String name;// 姓名模糊匹配
    private String username;// 用户名精确匹配
    private Integer role;


    @Min(value = 1, message = "页码最小为1")
    private int pageNum = 1;

    @Min(value = 1, message = "每页最小1条")
    @Max(value = 100, message = "每页最多100条")
    private int pageSize = 10;
}
