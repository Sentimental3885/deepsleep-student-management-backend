package com.deepsleep.service.Impl;


import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.StudentProfileVO;
import com.deepsleep.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private DeptMapper deptMapper;
    @Resource
    private MajorMapper majorMapper;
    @Resource
    private ClazzMapper clazzMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private FileStorage fileStorage;



    @Override
    public StudentProfileVO getStudentProfile() {
        Long userId = UserContext.getUserId();
        Student student = studentMapper.selectById(userId);
        User user = userMapper.selectById(userId);
        StudentProfileVO vo = new StudentProfileVO();
        if (user != null) {
            vo.setAvatar(fileStorage.getUrl(user.getAvatar()));
        }
        vo.setDeptId(student.getDeptId());
        vo.setMajorId(student.getMajorId());
        vo.setClazzId(student.getClazzId());
        vo.setPosition(student.getPosition());
        vo.setEntryDate(student.getEntryDate());
        vo.setDeptName(deptMapper.selectById(student.getDeptId()).getName());
        vo.setMajorName(majorMapper.selectById(student.getMajorId()).getName());
        vo.setClazzName(clazzMapper.selectById(student.getClazzId()).getName());
        return vo;
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
