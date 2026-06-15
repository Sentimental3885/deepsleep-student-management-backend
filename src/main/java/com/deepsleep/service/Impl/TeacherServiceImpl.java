package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.TeacherOptionQueryDTO;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.po.Classroom;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSchedule;
import com.deepsleep.data.po.Dept;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherOptionVO;
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

    @Override
    public IPage<TeacherOptionVO> getTeacherOptions(TeacherOptionQueryDTO dto) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getDeptId() != null, Teacher::getDeptId, dto.getDeptId())
                .exists(dto.getKeyword() != null && !dto.getKeyword().isBlank(),
                        "SELECT 1 FROM user u WHERE u.id = teacher.user_id " +
                                "AND (u.name LIKE {0} OR u.username LIKE {0})",
                        "%" + dto.getKeyword() + "%")
                .orderByAsc(Teacher::getUserId);
        Page<Teacher> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return teacherMapper.selectPage(page, wrapper).convert(this::toTeacherOptionVO);
    }

    private TeacherOptionVO toTeacherOptionVO(Teacher teacher) {
        User user = userMapper.selectById(teacher.getUserId());
        Dept dept = deptMapper.selectById(teacher.getDeptId());
        TeacherOptionVO vo = new TeacherOptionVO();
        vo.setId(teacher.getUserId());
        vo.setDeptId(teacher.getDeptId());
        vo.setTitle(teacher.getTitle());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setName(user.getName());
        }
        if (dept != null) {
            vo.setDeptName(dept.getName());
        }
        return vo;
    }
}
