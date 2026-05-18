package com.deepsleep.data.vo;

import lombok.Data;

@Data
public class UserProfileVO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer role;
    // 学生扩展信息，非学生时为null
    private StudentInfoVO studentInfo;
    // 教师扩展信息，非教师时为null
    private TeacherInfoVO teacherInfo;
}
