package com.deepsleep.service.Impl;

import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.file.storage.FileStorage;
import com.deepsleep.mapper.DeptMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final DeptMapper deptMapper;
    private final UserMapper userMapper;
    private final FileStorage fileStorage;

    @Override
    public TeacherProfileVO getTeacherProfile() {
        Long userId = UserContext.getUserId();
        Teacher teacher = teacherMapper.selectById(userId);
        User user = userMapper.selectById(userId);
        TeacherProfileVO vo = new TeacherProfileVO();
        if (user != null) {
            vo.setAvatar(fileStorage.getUrl(user.getAvatar()));
        }
        vo.setDeptId(teacher.getDeptId());
        vo.setTitle(teacher.getTitle());
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
