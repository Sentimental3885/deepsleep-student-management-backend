package com.deepsleep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.dto.AddTeacherDTO;
import com.deepsleep.data.dto.UserQueryDTO;
import com.deepsleep.data.vo.AdminUserDetailVO;
import com.deepsleep.data.vo.AdminUserVO;


public interface AdminService {
    void addTeacher(AddTeacherDTO dto);
    void addStudent(AddStudentDTO dto);
    void resetPassword(Long userId);
    void deleteUser(Long userId);
    Page<AdminUserVO> getUserList(UserQueryDTO dto);
    AdminUserDetailVO getUserDetail(Long userId);
}
