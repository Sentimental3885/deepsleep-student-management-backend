package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.dto.AddTeacherDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;


    //根据学生信息自动创建user和student条目
    @Transactional
    @Override
    public void addStudent(AddStudentDTO dto) {

        verifySsid(dto.getSsid());
        /*
        不知是否要验证d/m/z，总之先预留位置
        */

        User user = new User();
        user.setUsername(dto.getSsid().toString());
        user.setName(dto.getName());
        user.setGender(dto.getGender());
        user.setRole(RoleEnum.STUDENT.getCode());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //默认密码是A+学号后六位+@
        String pwd = "A" + String.valueOf(dto.getSsid() % 1000000) + '@';
        user.setPasswordHash(BCrypt.hashpw(pwd,BCrypt.gensalt()));
        userMapper.insert(user);
        //自增主键，自动回填sid
        Student student = new Student(
                user.getId(), dto.getDid(), dto.getMid(), dto.getZid(), null, dto.getEntryDate()
        );
        studentMapper.insert(student);
    }

    //验证学号
    private void verifySsid(Long ssid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, ssid);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.STUDENT_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional
    public void addTeacher(AddTeacherDTO dto) {
        // 检查工号是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getTsid().toString())
        );
        if (count > 0) throw new BusinessException(ResultCode.TEACHER_ALREADY_EXIST);

        // 默认密码 A+工号后六位+@
        String pwd = "A" + String.valueOf(dto.getTsid() % 1000000) + '@';

        User user = new User();
        user.setUsername(dto.getTsid().toString());
        user.setName(dto.getName());
        user.setGender(dto.getGender());
        user.setRole(RoleEnum.TEACHER.getCode());
        user.setPasswordHash(BCrypt.hashpw(pwd, BCrypt.gensalt()));
        userMapper.insert(user);

        Teacher teacher = new Teacher();
        teacher.setUserId(user.getId());
        teacher.setDeptId(dto.getDid());
        teacher.setTitle(dto.getTitle());
        teacher.setEntryDate(dto.getEntryDate());
        teacherMapper.insert(teacher);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND);

        // 重置为A+用户名后六位+@
        String username = user.getUsername();
        String pwd = "A" + username.substring(Math.max(0, username.length() - 6)) + '@';
        User update = new User();
        update.setId(userId);
        update.setPasswordHash(BCrypt.hashpw(pwd, BCrypt.gensalt()));
        userMapper.updateById(update);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND);


        if (user.getRole() == RoleEnum.STUDENT.getCode()) {
            studentMapper.deleteById(userId);
        } else if (user.getRole() == RoleEnum.TEACHER.getCode()) {
            teacherMapper.deleteById(userId);
        }

        userMapper.deleteById(userId);
    }
}
