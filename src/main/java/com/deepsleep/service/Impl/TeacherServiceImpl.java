package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.CourseMapper;
import com.deepsleep.mapper.DeptMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.SelectionService;
import com.deepsleep.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final DeptMapper deptMapper;
    private final UserMapper userMapper;
    private final FileStorage fileStorage;
    private final CourseMapper courseMapper;
    private final SelectionService selectionService;

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

    @Override
    public List<TeacherCourseVO> getMyCourses() {
        Long userId = UserContext.getUserId();
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId)
                .orderByDesc(Course::getCreateTime)
        );
        return courses.stream().map(course -> {
            TeacherCourseVO vo = new TeacherCourseVO();
            vo.setId(course.getId());
            vo.setName(course.getName());
            vo.setCode(course.getCode());
            vo.setSemester(course.getSemester());
            vo.setCapacity(course.getCapacity());
            vo.setSize(selectionService.currentSize(course.getId()));
            vo.setCredit(course.getCredit());
            vo.setStatus(course.getStatus().getValue());
            return vo;
        }).toList();
    }
}
