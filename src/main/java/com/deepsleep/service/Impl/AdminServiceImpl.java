package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.*;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.AdminUserDetailVO;
import com.deepsleep.data.vo.AdminUserVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final DeptMapper deptMapper;
    private final MajorMapper majorMapper;
    private final ClazzMapper clazzMapper;
    private final FileStorage fileStorage;


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

    @Override
    public Page<AdminUserVO> getUserList(UserQueryDTO dto) {
        Page<User> page = userMapper.selectPage(
                new Page<>(dto.getPageNum(),dto.getPageSize()),
                new LambdaQueryWrapper<User>().like(dto.getName()!=null,User::getName,dto.getName())
                        .eq(dto.getUsername()!=null,User::getUsername,dto.getUsername())
                        .eq(dto.getRole() != null,User::getRole,dto.getRole())
                        .orderByDesc(User::getCreateTime)
        );
        Page<AdminUserVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AdminUserVO> voList = page.getRecords().stream().map(user -> {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setName(user.getName());
            vo.setAvatar(fileStorage.getUrl(user.getAvatar()));
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setGender(user.getGender());
            vo.setRole(user.getRole());
            vo.setCreateTime(user.getCreateTime());
            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public AdminUserDetailVO getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND);

        AdminUserDetailVO vo = new AdminUserDetailVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(fileStorage.getUrl(user.getAvatar()));
        vo.setGender(user.getGender());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());

        if (user.getRole() == RoleEnum.STUDENT.getCode()) {
            Student student = studentMapper.selectById(userId);
            if (student != null) {
                AdminUserDetailVO.StudentInfo studentInfo = new AdminUserDetailVO.StudentInfo();
                studentInfo.setDeptId(student.getDeptId());
                studentInfo.setMajorId(student.getMajorId());
                studentInfo.setClazzId(student.getClazzId());
                studentInfo.setPosition(student.getPosition());
                studentInfo.setEntryDate(student.getEntryDate());
                studentInfo.setDeptName(deptMapper.selectById(student.getDeptId()).getName());
                studentInfo.setMajorName(majorMapper.selectById(student.getMajorId()).getName());
                studentInfo.setClazzName(clazzMapper.selectById(student.getClazzId()).getName());
                vo.setStudentInfo(studentInfo);
            }
        } else if (user.getRole() == RoleEnum.TEACHER.getCode()) {
            Teacher teacher = teacherMapper.selectById(userId);
            if (teacher != null) {
                AdminUserDetailVO.TeacherInfo teacherInfo = new AdminUserDetailVO.TeacherInfo();
                teacherInfo.setDeptId(teacher.getDeptId());
                teacherInfo.setTitle(teacher.getTitle());
                teacherInfo.setEntryDate(teacher.getEntryDate());
                teacherInfo.setDeptName(deptMapper.selectById(teacher.getDeptId()).getName());
                vo.setTeacherInfo(teacherInfo);
            }
        }

        return vo;
    }

    @Override
    public void updateUser(Long userId, AdminUpdateUserDTO dto) {
        User user = userMapper.selectById(userId);
        if(user==null) throw new BusinessException(ResultCode.NOT_FOUND);

        if(dto.getPhone()!=null){
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone,dto.getPhone())
                    .ne(User::getId,userId));
            if(count>0) throw new BusinessException(ResultCode.PHONE_CONFLICTED);
        }

        if (dto.getEmail()!=null) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, dto.getEmail())
                            .ne(User::getId, userId)
            );
            if (count>0) throw new BusinessException(ResultCode.EMAIL_CONFLICT);
        }

        User update = new User();
        update.setId(userId);
        update.setName(dto.getName());
        update.setGender(dto.getGender());
        update.setPhone(dto.getPhone());
        update.setEmail(dto.getEmail());
        userMapper.updateById(update);
    }


    @Override
    public void updateStudent(Long userId, AdminUpdateStudentDTO dto) {
        Student student = studentMapper.selectById(userId);
        if (student==null) throw new BusinessException(ResultCode.NOT_FOUND);

        Student update = new Student();
        update.setUserId(userId);
        update.setDeptId(dto.getDeptId());
        update.setMajorId(dto.getMajorId());
        update.setClazzId(dto.getClazzId());
        update.setPosition(dto.getPosition());
        update.setEntryDate(dto.getEntryDate());
        studentMapper.updateById(update);
    }

    @Override
    public void updateTeacher(Long userId, AdminUpdateTeacherDTO dto) {
        Teacher teacher = teacherMapper.selectById(userId);
        if (teacher==null) throw new BusinessException(ResultCode.NOT_FOUND);

        Teacher update = new Teacher();
        update.setUserId(userId);
        update.setDeptId(dto.getDeptId());
        update.setTitle(dto.getTitle());
        update.setEntryDate(dto.getEntryDate());
        teacherMapper.updateById(update);
    }
}
