package com.deepsleep.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private String name;
    private Integer role;  //0管理员 1教师 2学生
}