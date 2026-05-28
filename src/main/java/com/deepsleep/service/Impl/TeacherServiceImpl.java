package com.deepsleep.service.Impl;

import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.mapper.DeptMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final DeptMapper deptMapper;

    @Override
    public TeacherProfileVO getTeacherProfile() {
        Long userId = UserContext.getUserId();
        Teacher teacher = teacherMapper.selectById(userId);
        TeacherProfileVO vo = new TeacherProfileVO();
        vo.setDeptId(teacher.getDeptId());
        vo.setTitle(teacher.getTitle());;
        vo.setEntryDate(teacher.getEntryDate());
        vo.setDeptName(deptMapper.selectById(teacher.getDeptId()).getName());

        return vo;
    }

    @Override
    public void updateTeacherInfo(UpdateTeacherDTO dto) {
        Long userId = UserContext.getUserId();
        Teacher teacher = new Teacher();
        teacher.setUserId(userId);
        teacher.setTitle(dto.getTitle());
        teacher.setEntryDate(dto.getEntryDate());
        teacherMapper.updateById(teacher);
    }
}
