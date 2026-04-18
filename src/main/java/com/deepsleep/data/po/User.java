package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String name;
    private String passwordHash;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer role;   //0管理员，1教师，2学生
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}