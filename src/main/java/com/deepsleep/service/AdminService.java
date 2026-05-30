package com.deepsleep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.*;
import com.deepsleep.data.vo.AdminUserDetailVO;
import com.deepsleep.data.vo.AdminUserVO;


public interface AdminService {
    void addTeacher(AddTeacherDTO dto);
    void addStudent(AddStudentDTO dto);
    void resetPassword(Long userId);
    void deleteUser(Long userId);
    Page<AdminUserVO> getUserList(UserQueryDTO dto);
    AdminUserDetailVO getUserDetail(Long userId);
    void updateUser(Long userId, AdminUpdateUserDTO dto);
    void updateStudent(Long userId, AdminUpdateStudentDTO dto);
    void updateTeacher(Long userId, AdminUpdateTeacherDTO dto);
}
