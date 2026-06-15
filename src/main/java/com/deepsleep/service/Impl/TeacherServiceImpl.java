package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.po.Classroom;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSchedule;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.ClassroomMapper;
import com.deepsleep.mapper.CourseScheduleMapper;
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
    private final CourseScheduleMapper courseScheduleMapper;
    private final ClassroomMapper classroomMapper;
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

    @Override
    public List<ScheduleVO> getMySchedule() {
        Long userId = UserContext.getUserId();
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId)
        );
        if (courses.isEmpty()) return List.of();

        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        List<CourseSchedule> schedules = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .in(CourseSchedule::getCourseId, courseIds)
                        .orderByAsc(CourseSchedule::getWeekday)
                        .orderByAsc(CourseSchedule::getSection)
                        .orderByAsc(CourseSchedule::getStartWeek)
        );

        User teacher = userMapper.selectById(userId);
        return schedules.stream().map(schedule -> {
            Course course = courseMapper.selectById(schedule.getCourseId());
            Classroom classroom = classroomMapper.selectById(schedule.getClassroomId());
            ScheduleVO vo = new ScheduleVO();
            vo.setId(schedule.getId());
            vo.setCourseId(course.getId());
            vo.setCourseName(course.getName());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
                vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
            }
            vo.setWeekday(schedule.getWeekday());
            vo.setSection(schedule.getSection());
            vo.setStartWeek(schedule.getStartWeek());
            vo.setEndWeek(schedule.getEndWeek());
            if (classroom != null) {
                vo.setClassroomId(classroom.getId());
                vo.setClassroomName(classroom.getName());
            }
            return vo;
        }).toList();
    }
}
