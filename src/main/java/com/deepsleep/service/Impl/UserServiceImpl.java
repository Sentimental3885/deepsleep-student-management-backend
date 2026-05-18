package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateContactDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.StudentInfoVO;
import com.deepsleep.data.vo.TeacherInfoVO;
import com.deepsleep.data.vo.UserProfileVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.*;
import com.deepsleep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final DeptMapper deptMapper;
    private final MajorMapper majorMapper;
    private final ClazzMapper clazzMapper;


    @Override
    public UserProfileVO getProfile() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setRole(user.getRole());

        if (user.getRole() == 2) { // 学生
            Student student = studentMapper.selectById(userId);
            StudentInfoVO studentInfoVO = new StudentInfoVO();
            studentInfoVO.setDeptId(student.getDeptId());
            studentInfoVO.setMajorId(student.getMajorId());
            studentInfoVO.setClazzId(student.getClazzId());
            studentInfoVO.setPosition(student.getPosition());
            studentInfoVO.setEntryDate(student.getEntryDate());

            studentInfoVO.setDeptName(deptMapper.selectById(student.getDeptId()).getName());
            studentInfoVO.setMajorName(majorMapper.selectById(student.getMajorId()).getName());
            studentInfoVO.setClazzName(clazzMapper.selectById(student.getClazzId()).getName());
            vo.setStudentInfo(studentInfoVO);

        } else if (user.getRole() == 1) { // 教师
            Teacher teacher = teacherMapper.selectById(userId);
            TeacherInfoVO teacherInfoVO = new TeacherInfoVO();
            teacherInfoVO.setDeptId(teacher.getDeptId());
            teacherInfoVO.setTitle(teacher.getTitle());
            teacherInfoVO.setEntryDate(teacher.getEntryDate());
            teacherInfoVO.setDeptName(deptMapper.selectById(teacher.getDeptId()).getName());
            vo.setTeacherInfo(teacherInfoVO);
        }

        return vo;
    }

    @Override
    public void updateContact(UpdateContactDTO dto) {
        Long userId = UserContext.getUserId();

        if(dto.getPhone()!=null){
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone,dto.getPhone()).ne(User::getId,userId));
            if (count>0) throw new BusinessException(ResultCode.PHONE_CONFLICTED);
        }

        if (dto.getEmail() != null) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, dto.getEmail())
                            .ne(User::getId, userId)
            );
            if (count>0) throw new BusinessException(ResultCode.EMAIL_CONFLICT);
        }

        User user = new User();
        user.setId(userId);
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if(!BCrypt.checkpw(dto.getOldPassword(),user.getPasswordHash())) throw new BusinessException(ResultCode.PASSWORD_ERROR);

        User update = new User();
        update.setId(userId);
        update.setPasswordHash(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        userMapper.updateById(update);
    }

    @Override
    public void updateStudentInfo(UpdateStudentDTO dto) {
        Long userId = UserContext.getUserId();

        Student student = new Student();
        student.setUserId(userId);
        student.setClazzId(dto.getClazzId());
        student.setPosition(dto.getPosition());
        student.setEntryDate(dto.getEntryDate());
        studentMapper.updateById(student);
    }
}
