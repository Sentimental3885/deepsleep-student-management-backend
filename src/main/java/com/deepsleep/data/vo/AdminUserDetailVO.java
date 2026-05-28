package com.deepsleep.data.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdminUserDetailVO {

    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer role;
    private LocalDateTime createTime;
    // 学生扩展
    private StudentInfo studentInfo;
    // 教师扩展
    private TeacherInfo teacherInfo;

    @Data
    public static class StudentInfo {
        private Long deptId;
        private String deptName;
        private Long majorId;
        private String majorName;
        private Long clazzId;
        private String clazzName;
        private String position;
        private LocalDate entryDate;
    }

    @Data
    public static class TeacherInfo {
        private Long deptId;
        private String deptName;
        private String title;
        private LocalDate entryDate;
    }
}