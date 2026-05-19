package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.AddStudentDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.Result;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.StudentService;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private StudentMapper studentMapper;

    //根据学生信息自动创建user和student条目
    @Transactional
    @Override
    public Result<Void> addStudent(AddStudentDTO dto) {

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
        String pwd = 'A' + String.valueOf(dto.getSsid() % 1000000) + '@';
        user.setPasswordHash(BCrypt.hashpw(pwd,BCrypt.gensalt()));
        userMapper.insert(user);
        //自增主键，自动回填sid
        Student student = new Student(
                user.getId(), dto.getDid(), dto.getMid(), dto.getZid(), null, dto.getEntryDate()
        );
        studentMapper.insert(student);
        return Result.success();
    }

    //验证学号
    private void verifySsid(Long ssid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, ssid);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.STUDENT_ALREADY_EXIST);
        }
    }
}
