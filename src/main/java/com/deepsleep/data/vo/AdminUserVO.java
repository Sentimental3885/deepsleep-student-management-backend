package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员查询用户列表用
 */
@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private Integer gender;
    private Integer role;
    private LocalDateTime createTime;
}
